package dataStore.entity;

import java.nio.ByteBuffer;

public class IntegerEntity extends StoredEntity<Integer> {

    public IntegerEntity(Integer value) {
        super(value);
    }

    @Override
    public StoredEntityType getType() {
        return StoredEntityType.INTEGER;
    }

    @Override
    public byte[] serialize() {
        return ByteBuffer.allocate(4).putInt(value).array();
    }
}
