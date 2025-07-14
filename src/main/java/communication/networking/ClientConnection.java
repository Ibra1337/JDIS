package communication.networking;

import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

@Getter
@Setter
public class ClientConnection {
    private final SocketChannel channel;
    private final ByteBuffer buffer = ByteBuffer.allocate(1024);

    public ClientConnection(SocketChannel channel) {
        this.channel = channel;
    }


}
