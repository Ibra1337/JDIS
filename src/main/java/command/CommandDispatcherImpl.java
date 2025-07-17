package command;

import command.commandRes.CommandResult;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class CommandDispatcherImpl implements CommandDispatcher {

    private final CommandExecutor executor;

    public CommandResult dispatch(String commandName, List<String> args) {
        switch (commandName.toUpperCase()) {
            case "SET":
                return executor.executeSet(args);
            case "GET":
                return executor.executeGet(args);
            case "DEL":
                return executor.executeDel(args);
            case "RPUSH":
                return executor.executeRPush(args);
            case "LRANGE":
                return executor.executeLRange(args);
            case "LPOP":
                return executor.executeLPop(args);
            case "RPOP":
                return executor.executeRPop(args);
            case "TYPE":
                return executor.executeType(args);
            default:
                throw new UnsupportedOperationException("Unknown command: " + commandName);
        }
    }
}
