// Improved StoredEntity with better abstraction
package dataStore.entity;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


// Concrete implementations
public class StringEntity extends StoredEntity<String> {

    public StringEntity(String value) {
        super(value);
    }

    @Override
    public StoredEntityType getType() {
        return StoredEntityType.STRING;
    }

    @Override
    public byte[] serialize() {
        return value.getBytes(StandardCharsets.UTF_8);
    }
}
