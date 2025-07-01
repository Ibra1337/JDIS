//package presistance.encoding;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import dataStore.entity.StringEntity;
//import org.junit.jupiter.api.Test;
//import presistance.entry.DecodedEntry;
//
//import java.nio.ByteBuffer;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class EntryDecoderTest {
//
//    private final EntryDecoder decoder = new EntryDecoder();
//
//    private byte[] encodeStringEntry(String key, String value, Long expiration) {
//        byte[] keyBytes = key.getBytes();
//        byte[] valBytes = value.getBytes();
//
//        int totalSize = 1 + (expiration != null ? 8 + 1 : 0) + 4 + keyBytes.length + 4 + valBytes.length;
//        ByteBuffer buffer = ByteBuffer.allocate(totalSize);
//
//        if (expiration != null) {
//            buffer.put((byte) 0xFC);
//            buffer.putLong(expiration);
//        }
//
//        buffer.put((byte) 0x01); // type marker for STRING
//        buffer.putInt(keyBytes.length);
//        buffer.put(keyBytes);
//        buffer.putInt(valBytes.length);
//        buffer.put(valBytes);
//
//        return buffer.array();
//    }
//
//    @Test
//    public void testDecodeStringEntryWithoutExpiration() {
//        byte[] encoded = encodeStringEntry("testKey", "testValue", null);
//        DecodedEntry result = decoder.decode(encoded);
//
//        assertEquals("testKey", result.getKey());
//        assertNotNull(result.getStoredEntity());
//        assertTrue(result.getStoredEntity() instanceof StringEntity);
//        assertEquals("testValue", ((StringEntity) result.getStoredEntity()).getValue());
//        assertNull(result.getExpiration());
//    }
//
//    @Test
//    public void testDecodeStringEntryWithExpiration() {
//        long expiration = System.currentTimeMillis() + 1000;
//        byte[] encoded = encodeStringEntry("expKey", "expVal", expiration);
//        DecodedEntry result = decoder.decode(encoded);
//
//        assertEquals("expKey", result.getKey());
//        assertNotNull(result.getStoredEntity());
//        assertEquals("expVal", ((StringEntity) result.getStoredEntity()).getValue());
//        assertEquals(expiration, result.getExpiration());
//    }
//
//    @Test
//    public void testDecodeWithUnknownType() {
//        ByteBuffer buffer = ByteBuffer.allocate(10);
//        buffer.put((byte) 0x55); // unknown type marker
//        byte[] data = buffer.array();
//
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            decoder.decode(data);
//        });
//
//        assertTrue(exception.getMessage().contains("type not known"));
//    }
//
//    @Test
//    public void testDecodeWithTruncatedData() {
//        byte[] incomplete = new byte[]{0x01};
//        Exception exception = assertThrows(Exception.class, () -> {
//            decoder.decode(incomplete);
//        });
//
//        assertNotNull(exception);
//    }
//}
