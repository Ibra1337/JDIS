package command;



import command.commandRes.CommandResult;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CommandDispatcher {

    private final CommandExecutor executor;

    public CommandResult dispatch(String commandName, String... args) {
        switch (commandName.toUpperCase()) {
            case "SET":
                return executor.executeSet(args);
            case "GET":
                return executor.executeGet(args);
            case "DEL":
                return executor.executeDel(args);
            default:
                throw new UnsupportedOperationException("Unknown command: " + commandName);
        }
    }
}
