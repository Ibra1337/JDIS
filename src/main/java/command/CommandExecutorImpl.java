import dataStore.entity.StreamEntity;
import dataStore.entity.StoredEntity;
import dataStore.entity.StoredEntityType;
import dataStore.entity.StringEntity;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class CommandExecutorImpl implements CommandExecutor {

    private final DataStoreImpl dataStore;

    public CommandResult executeSet(List<String> args) {
        if (args.size() != 2) {
            throw new IllegalArgumentException("SET requires 2 arguments");
        }
        String key = args.get(0);
        String value = args.get(1);
        dataStore.set(key, new StringEntity(value));
        return new SimpleStringResult("OK");
    }

    public CommandResult executeGet(List<String> args) {
        if (args.size() != 1) {
            throw new IllegalArgumentException("GET requires 1 argument");
        }
        String key = args.get(0);
        var value = dataStore.get(key);
        if (value == null) {
            return new BulkStringResult(null);
        }
        return new BulkStringResult((String) value.getValue());
    }

    public CommandResult executeDel(List<String> args) {
        if (args.size() != 1) {
            throw new IllegalArgumentException("DEL requires 1 argument");
        }
        String key = args.get(0);
        boolean removed = dataStore.delete(key);
        return new IntegerResult(removed ? 1 : 0);
    }

    @Override
    public CommandResult executeListAdd(List<String> args) {
        if (args.size() < 2 ){
            return new ErrorResult("The RPUSH requires at least 2 aruments");
        }
        String key = args.get(0);
        var entry = dataStore.get(key);
        if (entry == null) {
            entry = new ListEntity(new LinkedList<>()) ;
            dataStore.set(key, entry);
        }

        if (entry.getType() != StoredEntityType.LIST)
            return new ErrorResult("The given key is associated with non-list value");

        var l = (List) entry.getValue();
        for (int i =1 ; i < args.size(); i++){
            l.add(args.get(i));
        }
        return new IntegerResult(l.size());
    }

    @Override
    public CommandResult executeLRange(List<String> args) {
        String key = args.get(0);
        int start = 0;
        int end;

        var entry = dataStore.get(key);
        if (entry == null || entry.getType() != StoredEntityType.LIST) {
            return new ArrayResult(new ArrayList<>());
        }

        var a = (ListEntity) entry;
        var l = (List<String>) a.getValue();
        end = l.size();

        if (args.size() >= 2) {
            try {
                start = Integer.parseInt(args.get(1));
            } catch (NumberFormatException e) {
                return new ErrorResult("ERR value is not an integer or out of range");
            }
        }

        if (args.size() == 3) {
            try {
                end = Integer.parseInt(args.get(2));
            } catch (NumberFormatException e) {
                return new ErrorResult("ERR value is not an integer or out of range");
            }
        }

        int size = l.size();
        if (start < 0) start = size + start;
        if (end < 0) end = size + end;

        start = Math.max(0, start);
        end = Math.min(end, size - 1);

        if (start > end || start >= size) {
            return new ArrayResult(new LinkedList<>());
        }

        List<CommandResult> result = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            result.add(new BulkStringResult(l.get(i)));
        }

        return new ArrayResult(result);
    }

    @Override
    public CommandResult executeRPush(List<String> args) {
        if (args.size() < 2) {
            return new ErrorResult("ERR wrong number of arguments for 'rpush' command");
        }

        String key = args.get(0);
        List<String> valuesToAdd = args.subList(1, args.size());

        var existingEntry = dataStore.get(key);
        List<String> targetList;

        if (existingEntry == null) {
            targetList = new ArrayList<>(valuesToAdd);
            dataStore.set(key, new ListEntity(targetList));
        } else if (existingEntry.getType() != StoredEntityType.LIST) {
            return new ErrorResult("WRONGTYPE Operation against a key holding the wrong kind of value");
        } else {
            var listEntity = (ListEntity) existingEntry;
            targetList = (List<String>) listEntity.getValue();
            targetList.addAll(valuesToAdd);
        }

        return new IntegerResult(targetList.size());
    }

    @Override
    public CommandResult executeLPop(List<String> args) {
        if (args.size() != 1) {
            return new ErrorResult("ERR wrong number of arguments for 'lpop' command");
        }

        String key = args.get(0);
        var entry = dataStore.get(key);

        if (entry == null || entry.getType() != StoredEntityType.LIST) {
            return new NullBulkStringResult();
        }

        var listEntity = (ListEntity) entry;
        var list = (LinkedList<String>) listEntity.getValue();

        if (list.isEmpty()) {
            return new NullBulkStringResult();
        }

        String popped = list.removeFirst();
        return new BulkStringResult(popped);
    }

    @Override
    public CommandResult executeRPop(List<String> args) {
        if (args.size() != 1) {
            return new ErrorResult("ERR wrong number of arguments for 'rpop' command");
        }

        String key = args.get(0);
        var entry = dataStore.get(key);

        if (entry == null || entry.getType() != StoredEntityType.LIST) {
            return new NullBulkStringResult();
        }

        var listEntity = (ListEntity) entry;
        var list = (LinkedList<String>) listEntity.getValue();

        if (list.isEmpty()) {
            return new NullBulkStringResult();
        }

        String popped = list.removeLast();
        return new BulkStringResult(popped);
    }

    @Override
    public CommandResult executeType(List<String> args) {
        if (args.size() != 1) {
            return new ErrorResult("ERR wrong number of arguments for 'type' command");
        }

        String key = args.get(0);
        var entry = dataStore.get(key);

        if (entry == null) {
            return new SimpleStringResult("none");
        }

        String typeName;
        switch (entry.getType()) {
            case STRING:
                typeName = "string";
                break;
            case LIST:
                typeName = "list";
                break;
            default:
                typeName = "unknown";
                break;
        }

        return new SimpleStringResult(typeName);
    }

    @Override
    public CommandResult executeXadd(List<String> args) {
        if (args.size() < 3 || args.size() % 2 != 1) {
            return new ErrorResult("ERR wrong number of arguments for 'xadd' command");
        }

        String key = args.get(0);
        String id = args.get(1);

        StoredEntity<?> entry = dataStore.get(key);
        StreamEntity streamEntity;

        if (entry == null) {
            streamEntity = new StreamEntity();
            dataStore.set(key, streamEntity);
        } else if (entry.getType() == StoredEntityType.STREAM) {
            streamEntity = (StreamEntity) entry;
        } else {
            return new ErrorResult("WRONGTYPE Operation against a key holding the wrong kind of value");
        }

        if (id.equals("*")) {
            id = (System.currentTimeMillis()) + "-0";
        }

        Map<String, String> fields = new HashMap<>();
        for (int i = 2; i < args.size(); i += 2) {
            fields.put(args.get(i), args.get(i + 1));
        }

        streamEntity.add(id, fields);

        return new BulkStringResult(id);
    }

    @Override
    public CommandResult executeXrange(List<String> args) {
        if (args.size() != 3) {
            return new ErrorResult("ERR wrong number of arguments for 'xrange' command");
        }

        String key = args.get(0);
        String start = args.get(1);
        String end = args.get(2);

        StoredEntity<?> entry = dataStore.get(key);
        if (entry == null || entry.getType() != StoredEntityType.STREAM) {
            return new ArrayResult(new ArrayList<>());
        }

        StreamEntity streamEntity = (StreamEntity) entry;
        var streamData = streamEntity.getValue();

        if (start.equals("-")) {
            start = streamData.firstKey();
        }
        if (end.equals("+")) {
            end = streamData.lastKey();
        }

        var subMap = streamData.subMap(start, true, end, true);

        List<CommandResult> result = new ArrayList<>();
        for (Map.Entry<String, Map<String, String>> streamEntry : subMap.entrySet()) {
            List<CommandResult> entryResult = new ArrayList<>();
            entryResult.add(new BulkStringResult(streamEntry.getKey()));

            List<CommandResult> fields = new ArrayList<>();
            for (Map.Entry<String, String> field : streamEntry.getValue().entrySet()) {
                fields.add(new BulkStringResult(field.getKey()));
                fields.add(new BulkStringResult(field.getValue()));
            }
            entryResult.add(new ArrayResult(fields));
            result.add(new ArrayResult(entryResult));
        }

        return new ArrayResult(result);
    }

    @Override
    public CommandResult executeXread(List<String> args) {
        if (args.size() < 4 || !args.get(0).equalsIgnoreCase("STREAMS")) {
            return new ErrorResult("ERR syntax error");
        }

        int streamKeysCount = (args.size() - 1) / 2;
        List<String> keys = args.subList(1, 1 + streamKeysCount);
        List<String> ids = args.subList(1 + streamKeysCount, args.size());

        List<CommandResult> result = new ArrayList<>();

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String id = ids.get(i);

            StoredEntity<?> entry = dataStore.get(key);
            if (entry == null || entry.getType() != StoredEntityType.STREAM) {
                continue;
            }

            StreamEntity streamEntity = (StreamEntity) entry;
            var streamData = streamEntity.getValue();

            var tailMap = streamData.tailMap(id, false);

            if (tailMap.isEmpty()) {
                continue;
            }

            List<CommandResult> streamResult = new ArrayList<>();
            streamResult.add(new BulkStringResult(key));

            List<CommandResult> entries = new ArrayList<>();
            for (Map.Entry<String, Map<String, String>> streamEntry : tailMap.entrySet()) {
                List<CommandResult> entryResult = new ArrayList<>();
                entryResult.add(new BulkStringResult(streamEntry.getKey()));

                List<CommandResult> fields = new ArrayList<>();
                for (Map.Entry<String, String> field : streamEntry.getValue().entrySet()) {
                    fields.add(new BulkStringResult(field.getKey()));
                    fields.add(new BulkStringResult(field.getValue()));
                }
                entryResult.add(new ArrayResult(fields));
                entries.add(new ArrayResult(entryResult));
            }
            streamResult.add(new ArrayResult(entries));
            result.add(new ArrayResult(streamResult));
        }

        return new ArrayResult(result);
    }
}
