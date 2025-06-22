package communication.networking;

import communication.Command;
import utiils.CommandHandler;
import utiils.Response;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestParserImpl implements RequestParser{



    public RequestParserImpl(){

    }

    @Override
    public Command parse(SocketChannel socketChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        socketChannel.read(buffer);
        String data = new String(buffer.array()).trim();
        //put
        if (data.length() > 0) {
            System.out.println("Received message: " + data);
            var space = data.indexOf(" ");
            if (space == -1 )
                space = data.length();

            String com = data.substring(0 , space ) ;
            System.out.println("===-> "  + data.substring(space));
            return new Command(com, List.of(data.substring(space)));

        }
        return null;
    }
}
