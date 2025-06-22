package communication.networking;

import communication.Command;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public interface RequestParser {

    Command parse(SocketChannel channel ) throws IOException;

}
