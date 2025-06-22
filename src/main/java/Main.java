
import communication.networking.TCPServer;

public class Main {
    public static void main(String[] args) {

        var server = new TCPServer("10.0.0.1" , 7000);
        server.listen();


    }
}


