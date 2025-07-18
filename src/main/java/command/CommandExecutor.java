package command;


import command.commandRes.CommandResult;

import java.util.List;

/**
 * Interface for executing commands on the data store.
 */
public interface CommandExecutor {
    /**
     * Sets a key-value pair.
     *
     * @param args The command arguments, where the first argument is the key and the second is the value.
     * @return The result of the command execution.
     */
    CommandResult executeSet(List<String> args);

    /**
     * Gets the value of a key.
     *
     * @param args The command arguments, where the first argument is the key.
     * @return The result of the command execution.
     */
    CommandResult executeGet(List<String> args);

    /**
     * Deletes a key.
     *
     * @param args The command arguments, where the first argument is the key.
     * @return The result of the command execution.
     */
    CommandResult executeDel(List<String> args);

    /**
     * Adds one or more elements to the beginning of a list.
     *
     * @param args The command arguments, where the first argument is the key of the list, and the following arguments are the elements to add.
     * @return The result of the command execution.
     */
    CommandResult executeListAdd(List<String> args);

    /**
     * Gets a range of elements from a list.
     *
     * @param args The command arguments, where the first argument is the key of the list, the second is the start index, and the third is the end index.
     * @return The result of the command execution.
     */
    CommandResult executeLRange(List<String> args);

    /**
     * Adds one or more elements to the end of a list.
     *
     * @param args The command arguments, where the first argument is the key of the list, and the following arguments are the elements to add.
     * @return The result of the command execution.
     */
    CommandResult executeRPush(List<String> args);

    /**
     * Removes and returns the first element of a list.
     *
     * @param args The command arguments, where the first argument is the key of the list.
     * @return The result of the command execution.
     */
    CommandResult executeLPop(List<String> args);

    /**
     * Removes and returns the last element of a list.
     *
     * @param args The command arguments, where the first argument is the key of the list.
     * @return The result of the command execution.
     */
    CommandResult executeRPop(List<String> args);

    /**
     * Returns the type of the value stored at a key.
     *
     * @param args The command arguments, where the first argument is the key.
     * @return The result of the command execution.
     */
    CommandResult executeType(List<String> args);

    CommandResult executeXadd(List<String> args);

    CommandResult executeXrange(List<String> args);

    CommandResult executeXread(List<String> args);
}
