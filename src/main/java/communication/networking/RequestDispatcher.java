package communication.networking;

import communication.Command;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface RequestDispatcher {



    String dispatchRequest(Command command, SocketChannel client) throws IOException;
}
