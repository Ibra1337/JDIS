
import communication.networking.TCPServer;

public class Main {
    public static void main(String[] args) {
        int port = 6379;
        if (args.length == 1 ){
            if (args[0].matches("\\d+"))
                port = Integer.parseInt(args[0]);
        }
        var server = new TCPServer("localhost" , port);
        server.listen();


    }
}


