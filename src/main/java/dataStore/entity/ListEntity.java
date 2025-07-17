package dataStore.entity;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ListEntity extends StoredEntity<List<String>> {

    public ListEntity(List<String> value) {
        super(new LinkedList<>(value));
    }

    @Override
    public StoredEntityType getType() {
        return StoredEntityType.LIST;
    }

    @Override
    public byte[] serialize() {

        int totalSize = 4;
        for (String item : value) {
            totalSize += 4 + item.getBytes(StandardCharsets.UTF_8).length;
        }

        ByteBuffer buffer = ByteBuffer.allocate(totalSize);
        buffer.putInt(value.size());

        for (String item : value) {
            byte[] itemBytes = item.getBytes(StandardCharsets.UTF_8);
            buffer.putInt(itemBytes.length);
            buffer.put(itemBytes);
        }

        return buffer.array();
    }


    public void add(String item) {
        value.add(item);
    }

    public String remove(int index) {
        return value.remove(index);
    }

    public String get(int index) {
        return value.get(index);
    }

    public int size() {
        return value.size();
    }

    public List<String> getRange(int start, int end) {
        int size = value.size();
        if (start < 0) start = Math.max(0, size + start);
        if (end < 0) end = size + end;

        start = Math.max(0, start);
        end = Math.min(size - 1, end);

        if (start > end) {
            return new ArrayList<>();
        }

        return new ArrayList<>(value.subList(start, end + 1));
    }
}
