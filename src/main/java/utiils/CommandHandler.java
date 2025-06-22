package utiils;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Collection;

@FunctionalInterface
public interface CommandHandler {
    String handleCommand(SocketChannel channel , Collection<String> args) throws IOException;

}
