package command;

import command.commandRes.CommandResult;

import java.util.List;

public interface CommandDispatcher {
    CommandResult dispatch(String commandName, List<String> args);
}
