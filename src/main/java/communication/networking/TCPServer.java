package communication.networking;

import command.CommandDispatcher;
import command.CommandDispatcherImpl;
import command.CommandExecutor;
import command.CommandExecutorImpl;
import communication.parser.RespDecoder;
import communication.parser.RequestReader;
import dataStore.DataStoreImpl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class TCPServer implements Server {

    private final String host;
    private final int port;
    private final RequestReader requestReader;
    private final CommandDispatcher commandDispatcher;
    private final DataStoreImpl dataStore = new DataStoreImpl();
    private final CommandExecutor commandExecutor;
    private Selector selector;

    public TCPServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.requestReader = new RespDecoder();
        this.commandExecutor = new CommandExecutorImpl(dataStore);
        this.commandDispatcher = new CommandDispatcherImpl(this.commandExecutor);
    }

    @Override
    public void listen() {
        System.out.println("Listening on port " + this.port);
        try {
            selector = Selector.open();

            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(host, port));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> i = selectedKeys.iterator();

                while (i.hasNext()) {
                    SelectionKey key = i.next();
                    i.remove();

                    try {
                        if (key.isAcceptable()) {
                            handleAccept(serverSocketChannel);
                        } else if (key.isReadable()) {
                            handleRead(key);
                        } else if (key.isWritable()) {
                            handleWrite(key);
                        }
                    } catch (IOException e) {
                        System.err.println("Client error: " + e.getMessage());
                        key.cancel();
                        try {
                            key.channel().close();
                        } catch (IOException ce) {
                            ce.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleAccept(ServerSocketChannel serverSocketChannel) throws IOException {
        System.out.println("Connection accepted...");
        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }

    private void handleRead(SelectionKey key) throws IOException {
        System.out.println("Reading...");
        SocketChannel channel = (SocketChannel) key.channel();
        ClientConnection clientConnection = new ClientConnection(channel);

        var com = this.requestReader.read(clientConnection);

        if (com == null) {
            System.out.println("Client disconnected or sent empty command.");
            key.cancel();
            channel.close();
            return;
        }

        if (com.getName().equalsIgnoreCase("QUIT")) {
            System.out.println("Closing connection by client request.");
            key.cancel();
            channel.close();
            return;
        }

        var res = this.commandDispatcher.dispatch(com.getName(), com.getArgs());
        var resp = res.toRespFormat();

        byte[] bArr = resp.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buff = ByteBuffer.wrap(bArr);

        channel.write(buff);

        if (buff.hasRemaining()) {
            key.attach(buff);
            key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
        }
    }

    private void handleWrite(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buff = (ByteBuffer) key.attachment();

        if (buff != null) {
            channel.write(buff);

            if (!buff.hasRemaining()) {
                key.attach(null);
                key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
            }
        }
    }
}

