package communication.parser;

import communication.Command;
import communication.networking.ClientConnection;

import java.io.IOException;

public interface RequestReader {

    public Command read(ClientConnection connection) throws IOException;
}
