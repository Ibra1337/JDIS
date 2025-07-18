package presistance.encoding;

import dataStore.entity.StoredEntity;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

public class Encoder {

    public ByteBuffer encodeEntity(String key, StoredEntity value, Long expirationMillis) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        var expLen = 0;
        if (expirationMillis != null)
            expLen = 9;

        var entityBytes = encodeEntity(value);

        ByteBuffer buf = ByteBuffer.allocate(expLen  + 1 + 4 +keyBytes.length  +  entityBytes.length  );
        if (expLen!=0) {
            buf.put((byte) 0xFC);
            buf.putLong(expirationMillis);
        }
        buf.put(value.getType().getTypeMarker());
        buf.putInt(key.length());
        buf.put(keyBytes);
        buf.put(entityBytes);

        return buf;

    }


    private byte[] encodeEntity(StoredEntity<?> entity){
        switch (entity.getType()){
            case STRING: {
                var val = (String) entity.getValue();
                var valBytes = val.getBytes(StandardCharsets.UTF_8);

                ByteBuffer buffer = ByteBuffer.allocate(4 + valBytes.length);
                buffer.putInt(valBytes.length);
                buffer.put(valBytes);
                return buffer.array();
            }
            case STREAM: {
                var stream = (TreeMap<String, Map<String, String>>) entity.getValue();

                // Calculate total size
                int totalSize = 4; // for number of entries
                for (Map.Entry<String, Map<String, String>> streamEntry : stream.entrySet()) {
                    totalSize += 4 + streamEntry.getKey().getBytes(StandardCharsets.UTF_8).length; // for ID
                    totalSize += 4; // for number of pairs
                    for (Map.Entry<String, String> pair : streamEntry.getValue().entrySet()) {
                        totalSize += 4 + pair.getKey().getBytes(StandardCharsets.UTF_8).length; // for pair key
                        totalSize += 4 + pair.getValue().getBytes(StandardCharsets.UTF_8).length; // for pair value
                    }
                }

                ByteBuffer streamBuffer = ByteBuffer.allocate(totalSize);
                streamBuffer.putInt(stream.size());

                for (Map.Entry<String, Map<String, String>> streamEntry : stream.entrySet()) {
                    byte[] idBytes = streamEntry.getKey().getBytes(StandardCharsets.UTF_8);
                    streamBuffer.putInt(idBytes.length);
                    streamBuffer.put(idBytes);

                    Map<String, String> pairs = streamEntry.getValue();
                    streamBuffer.putInt(pairs.size());

                    for (Map.Entry<String, String> pair : pairs.entrySet()) {
                        byte[] keyBytes = pair.getKey().getBytes(StandardCharsets.UTF_8);
                        streamBuffer.putInt(keyBytes.length);
                        streamBuffer.put(keyBytes);

                        byte[] valueBytes = pair.getValue().getBytes(StandardCharsets.UTF_8);
                        streamBuffer.putInt(valueBytes.length);
                        streamBuffer.put(valueBytes);
                    }
                }
                return streamBuffer.array();
            }
            default:
                System.err.println("error no handler found for type: " + entity.getType());
                return null;
        }
    }




}
