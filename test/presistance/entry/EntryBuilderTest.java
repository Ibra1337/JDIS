package presistance.entry;

import dataStore.entity.StringEntity;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class EntryBuilderTest {

    @Test
    void testDecodeStringEntryWithoutExpiration() {
        EntryBuilder builder = new EntryBuilder();

        // Build a test entry: [TYPE][KEY_LEN][KEY][VAL_LEN][VAL]
        byte type = 0x01;
        String key = "testKey";
        String value = "HelloWorld";

        byte[] keyBytes = key.getBytes();
        byte[] valueBytes = value.getBytes();

        int totalLen = 1 + 4 + keyBytes.length + 4 + valueBytes.length;
        ByteBuffer buf = ByteBuffer.allocate(totalLen);
        buf.put(type);
        buf.putInt(keyBytes.length);
        buf.put(keyBytes);
        buf.putInt(valueBytes.length);
        buf.put(valueBytes);
        buf.flip();

        builder.consume(buf);
        assertTrue(builder.isComplete());

        DecodedEntry decoded = builder.build();
        assertEquals(key, decoded.getKey());
        assertTrue(decoded.getStoredEntity() instanceof StringEntity);
        assertEquals(value, ((StringEntity) decoded.getStoredEntity()).getValue());
        assertNull(decoded.getExpiration());
    }

    @Test
    void testDecodeStringEntryWithExpiration() {
        EntryBuilder builder = new EntryBuilder();

        byte type = 0x01;
        String key = "expKey";
        String value = "Goodbye";
        long expiration = 123456789L;

        byte[] keyBytes = key.getBytes();
        byte[] valueBytes = value.getBytes();

        int totalLen = 1 + 8 + 1 + 4 + keyBytes.length + 4 + valueBytes.length;
        ByteBuffer buf = ByteBuffer.allocate(totalLen);
        buf.put((byte) 0xFC);           // expiration flag
        buf.putLong(expiration);
        buf.put(type);
        buf.putInt(keyBytes.length);
        buf.put(keyBytes);
        buf.putInt(valueBytes.length);
        buf.put(valueBytes);
        buf.flip();

        builder.consume(buf);
        assertTrue(builder.isComplete());

        DecodedEntry decoded = builder.build();
        assertEquals(key, decoded.getKey());
        assertTrue(decoded.getStoredEntity() instanceof StringEntity);
        assertEquals(value, ((StringEntity) decoded.getStoredEntity()).getValue());
        assertEquals(expiration, decoded.getExpiration());
    }

    @Test
    void testDecodeInChunks() {
        EntryBuilder builder = new EntryBuilder();

        byte type = 0x01;
        String key = "chunky";
        String value = "PartialBuffer";

        byte[] keyBytes = key.getBytes();
        byte[] valueBytes = value.getBytes();

        int totalLen = 1 + 4 + keyBytes.length + 4 + valueBytes.length;
        ByteBuffer buf = ByteBuffer.allocate(totalLen);
        buf.put(type);
        buf.putInt(keyBytes.length);
        buf.put(keyBytes);
        buf.putInt(valueBytes.length);
        buf.put(valueBytes);
        buf.flip();

        ByteBuffer part1 = ByteBuffer.allocate(5);
        buf.get(part1.array());
        part1.limit(5);

        ByteBuffer part2 = ByteBuffer.allocate(totalLen - 5);
        buf.get(part2.array());
        part2.limit(totalLen - 5);

        builder.consume(part1);
        assertFalse(builder.isComplete());

        builder.consume(part2);
        assertTrue(builder.isComplete());

        DecodedEntry decoded = builder.build();
        assertEquals(key, decoded.getKey());
        assertEquals(value, ((StringEntity) decoded.getStoredEntity()).getValue());
    }
}
