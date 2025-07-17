package command;


import command.commandRes.CommandResult;

import java.util.List;

public interface CommandExecutor {
    CommandResult executeSet(List<String> args);

    CommandResult executeGet(List<String> args);

    CommandResult executeDel(List<String> args);

    CommandResult executeListAdd(List<String> args);

    CommandResult executeLRange(List<String> args);

    CommandResult executeRPush(List<String> args);

    CommandResult executeLPop(List<String> args);

    CommandResult executeRPop(List<String> args);

    CommandResult executeType(List<String> args);
}
