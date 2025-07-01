package presistance.encoding;

import dataStore.entity.StoredEntityType;
import dataStore.entity.StringEntity;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class EntryEncoderTest {

    @Test
    void encodeStringNoExp() {
        StringEntity se = new StringEntity("lol");
        String key = "key";
        var keyBytes = key.getBytes(StandardCharsets.UTF_8);
        var sebytes = "lol".getBytes(StandardCharsets.UTF_8);
        Encoder entryEncoder = new Encoder();

        var res = entryEncoder.encodeEntity(key, se, null).array();



        ByteBuffer bb = ByteBuffer.allocate(1 + 4+ keyBytes.length + 4+sebytes.length );
        bb.put(StoredEntityType.STRING.getTypeMarker());
        bb.putInt(keyBytes.length);
        bb.put(keyBytes);
        bb.putInt(sebytes.length);
        bb.put(sebytes);

        assertArrayEquals(res , bb.array());


    }

    @Test
    void encodeString() {
        StringEntity se = new StringEntity("lol");
        String key = "key";
        var keyBytes = key.getBytes(StandardCharsets.UTF_8);
        var sebytes = "lol".getBytes(StandardCharsets.UTF_8);
        Long exp = 100235412L;
        Encoder entryEncoder = new Encoder();


        var res = entryEncoder.encodeEntity(key, se, 100235412L).array();



        ByteBuffer bb = ByteBuffer.allocate(1 + 9 + 4+ keyBytes.length + 4+sebytes.length );
        bb.put((byte) 0xFC);
        bb.putLong(exp);
        bb.put(StoredEntityType.STRING.getTypeMarker());
        bb.putInt(keyBytes.length);
        bb.put(keyBytes);
        bb.putInt(sebytes.length);
        bb.put(sebytes);

        assertArrayEquals(res , bb.array());


    }
}
