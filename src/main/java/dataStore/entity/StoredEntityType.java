package dataStore.entity;

public enum StoredEntityType {
    STRING((byte) 0x01),
    INTEGER((byte) 0x02),
    LIST((byte) 0x03),
    HASH((byte) 0x04),
    SET((byte) 0x05),
    SORTED_SET((byte) 0x06),
    DOUBLE((byte) 0x07),
    STREAM((byte) 0x08);

    private final byte typeMarker;

    StoredEntityType(byte typeMarker) {
        this.typeMarker = typeMarker;
    }

    public byte getTypeMarker() {
        return typeMarker;
    }

    public static StoredEntityType fromMarker(byte marker) {
        for (StoredEntityType type : values()) {
            if (type.typeMarker == marker) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown type marker: " + marker);
    }
}
