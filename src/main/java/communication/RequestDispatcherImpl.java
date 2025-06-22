package communication;

import communication.networking.RequestDispatcher;
import dataStore.DataStore;
import dataStore.StringDataStore;
import utiils.CommandHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class RequestDispatcherImpl implements RequestDispatcher {

    private final DataStore<String, String> dataStore = new StringDataStore();
    private Map<String, CommandHandler> commandHandlers = new HashMap<>();

    public RequestDispatcherImpl(){
        registerHandlers();
    }
    @Override
    public String dispatchRequest(Command command, SocketChannel client) throws IOException {
        var exec = commandHandlers.get(command.getCommand());

        if (exec == null)
            return "Invalid command";
        return exec.handleCommand(client , command.getArgs());
    }

    private void registerHandlers() {
        commandHandlers.put("exit", (client, args) -> {
            client.close();
            System.out.println("Connection closed...");
            return "OK";
        });

        commandHandlers.put("echo", (client, args) -> {
            ByteBuffer response = ByteBuffer.wrap(("ECHO: " + args).getBytes());
            client.write(response);
            return "OK";
        });

        commandHandlers.put("time", (client, args) -> {
            String time = java.time.LocalDateTime.now().toString();
            client.write(ByteBuffer.wrap(time.getBytes()));
            return "OK";

        });

        commandHandlers.put("ping" , (client,args) -> {
            String resp = "pong";
            client.write(ByteBuffer.wrap(resp.getBytes()));
            return "OK";
        });
        commandHandlers.put("put" , (client,args) -> {
            System.out.println("handle put");
            var data = args.stream().findFirst().get().split(";");

            dataStore.set(data[0] , data[1]);
            System.out.println("sending " + data[1]);
            client.write(ByteBuffer.wrap(data[1].getBytes() ));
            return "OK";
        });
        commandHandlers.put("get" , (client,args) -> {
            var data = args.stream().findFirst().get().split(";");
            client.write(ByteBuffer.wrap(dataStore.get(data[0]).getBytes() ));
            return "OK";


        });
    }
}
