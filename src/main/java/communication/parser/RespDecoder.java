package communication.parser;

import communication.Command;
import communication.networking.ClientConnection;
import communication.parser.token.ArrayToken;
import communication.parser.token.BulkStringToken;
import communication.parser.token.RespToken;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class RespDecoder implements RequestReader {

    @Override
    public Command read(ClientConnection connection) throws IOException {
        ByteBuffer buf = connection.getBuffer();
        SocketChannel channel = connection.getChannel();

        int bytesRead = channel.read(buf);
        if (bytesRead == -1) {
            throw new IOException("Client closed connection");
        }

        buf.flip();

        RespToken token = readToken(buf);

        buf.compact();

        if (!(token instanceof ArrayToken arrayToken)) {
            throw new IllegalStateException("Expected RESP Array as top-level request");
        }

        List<RespToken> elements = arrayToken.getElements();
        if (elements.isEmpty()) {
            throw new IllegalStateException("Empty command array");
        }

        RespToken first = elements.get(0);
        if (!(first instanceof BulkStringToken)) {
            throw new IllegalStateException("Command must be Bulk String");
        }

        String commandName = ((BulkStringToken) first).getValue();

        List<String> args = new ArrayList<>();
        for (int i = 1; i < elements.size(); i++) {
            RespToken elem = elements.get(i);
            if (!(elem instanceof BulkStringToken)) {
                throw new IllegalStateException("Argument must be Bulk String");
            }
            args.add(((BulkStringToken) elem).getValue());
        }

        return new Command(commandName, args);
    }

    private RespToken readToken(ByteBuffer buf) {
        if (!buf.hasRemaining()) {
            throw new IllegalStateException("Buffer is empty");
        }

        byte type = buf.get();

        return switch (type) {
            case '*' -> handleArray(buf);
            case '$' -> new BulkStringToken(handleBulkString(buf));
            default -> throw new IllegalArgumentException(
                    "Unsupported RESP type in client request: " + (char) type);
        };
    }

    private ArrayToken handleArray(ByteBuffer buf) {
        int len = readLength(buf);

        List<RespToken> elements = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            elements.add(readToken(buf));
        }
        return new ArrayToken(elements);
    }

    private String handleBulkString(ByteBuffer buf) {
        int strLen = readLength(buf);

        if (buf.remaining() < strLen + 2) {
            throw new IllegalStateException("Not enough bytes for bulk string");
        }

        byte[] data = new byte[strLen];
        buf.get(data);

        skipCRLF(buf);

        return new String(data);
    }

    private int readLength(ByteBuffer buf) {
        StringBuilder sb = new StringBuilder();
        while (buf.hasRemaining()) {
            byte b = buf.get();
            if (b == '\r') {
                if (buf.get() != '\n') {
                    throw new IllegalStateException("Invalid line ending after length");
                }

                break;
            }
            sb.append((char) b);
        }
        return Integer.parseInt(sb.toString());
    }

    private void skipCRLF(ByteBuffer buf) {
        if (buf.get() != '\r' || buf.get() != '\n') {
            throw new IllegalStateException("Expected CRLF");
        }
    }
}
