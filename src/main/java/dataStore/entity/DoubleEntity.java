package dataStore.entity;

import java.nio.ByteBuffer;

public class DoubleEntity extends StoredEntity<Double> {

    public DoubleEntity(Double value) {
        super(value);
    }

    @Override
    public StoredEntityType getType() {
        return StoredEntityType.DOUBLE;
    }

    @Override
    public byte[] serialize() {
        return ByteBuffer.allocate(8).putDouble(value).array();
    }
}
