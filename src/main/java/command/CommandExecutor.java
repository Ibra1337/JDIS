package command;

import command.commandRes.BulkStringResult;
import command.commandRes.CommandResult;
import command.commandRes.IntegerResult;
import command.commandRes.SimpleStringResult;
import dataStore.DataStoreImpl;
import dataStore.entity.StoredEntity;
import dataStore.entity.StringEntity;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CommandExecutor {

    private final DataStoreImpl dataStore;

    public CommandResult executeSet(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("SET requires 2 arguments");
        }
        String key = args[0];
        String value = args[1];
        dataStore.set(key, new StringEntity(value));
        return new SimpleStringResult("OK");
    }

    public CommandResult executeGet(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("GET requires 1 argument");
        }
        String key = args[0];
        var value = dataStore.get(key);
        return new BulkStringResult((String) value.getValue());
    }

    public CommandResult executeDel(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("DEL requires 1 argument");
        }
        String key = args[0];
        boolean removed = dataStore.delete(key);
        return  new IntegerResult( removed? 1 : 0);
    }
}
