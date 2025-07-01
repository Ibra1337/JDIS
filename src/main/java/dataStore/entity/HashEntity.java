package dataStore.entity;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HashEntity extends StoredEntity<Map<String, String>> {

    public HashEntity(Map<String, String> value) {
        super(new HashMap<>(value)); // Defensive copy
    }

    public HashEntity() {
        super(new HashMap<>());
    }

    @Override
    public StoredEntityType getType() {
        return StoredEntityType.HASH;
    }

    @Override
    public byte[] serialize() {
        // Calculate total size
        int totalSize = 4; // for map size
        for (Map.Entry<String, String> entry : value.entrySet()) {
            totalSize += 4 + entry.getKey().getBytes(StandardCharsets.UTF_8).length;
            totalSize += 4 + entry.getValue().getBytes(StandardCharsets.UTF_8).length;
        }

        ByteBuffer buffer = ByteBuffer.allocate(totalSize);
        buffer.putInt(value.size());

        for (Map.Entry<String, String> entry : value.entrySet()) {
            byte[] keyBytes = entry.getKey().getBytes(StandardCharsets.UTF_8);
            byte[] valueBytes = entry.getValue().getBytes(StandardCharsets.UTF_8);

            buffer.putInt(keyBytes.length);
            buffer.put(keyBytes);
            buffer.putInt(valueBytes.length);
            buffer.put(valueBytes);
        }

        return buffer.array();
    }

    // Hash-specific methods
    public String put(String field, String fieldValue) {
        return value.put(field, fieldValue);
    }

    public String get(String field) {
        return value.get(field);
    }

    public String remove(String field) {
        return value.remove(field);
    }

    public Set<String> getFields() {
        return value.keySet();
    }

    public boolean containsField(String field) {
        return value.containsKey(field);
    }

    public int size() {
        return value.size();
    }
}
