package presistance.encoding;

import dataStore.entity.StoredEntity;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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
            case STRING :
                var val = (String) entity.getValue();
                var valBytes = val.getBytes(StandardCharsets.UTF_8);

                ByteBuffer buffer = ByteBuffer.allocate(  4 + valBytes.length  );
                buffer.putInt(valBytes.length);
                buffer.put(valBytes);
                return buffer.array();

            default:
                System.err.println("error no handler found for type: " + entity.getType());
                return null;
        }
    }




}
