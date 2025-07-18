
package dataStore.entity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

public class StreamEntity extends StoredEntity<TreeMap<String, Map<String, String>>> {

    public StreamEntity() {
        super(new TreeMap<>());
    }

    public StreamEntity(TreeMap<String, Map<String, String>> value) {
        super(value);
    }

    @Override
    public StoredEntityType getType() {
        return StoredEntityType.STREAM;
    }

    @Override
    public byte[] serialize() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(value.size());
            for (Map.Entry<String, Map<String, String>> streamEntry : value.entrySet()) {
                byte[] idBytes = streamEntry.getKey().getBytes(StandardCharsets.UTF_8);
                dos.writeInt(idBytes.length);
                dos.write(idBytes);

                Map<String, String> pairs = streamEntry.getValue();
                dos.writeInt(pairs.size());

                for (Map.Entry<String, String> pair : pairs.entrySet()) {
                    byte[] keyBytes = pair.getKey().getBytes(StandardCharsets.UTF_8);
                    dos.writeInt(keyBytes.length);
                    dos.write(keyBytes);

                    byte[] valueBytes = pair.getValue().getBytes(StandardCharsets.UTF_8);
                    dos.writeInt(valueBytes.length);
                    dos.write(valueBytes);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return baos.toByteArray();
    }

    public void add(String id, Map<String, String> entry) {
        this.getValue().put(id, entry);
    }
}
