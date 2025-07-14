package communication;

public enum RespType {
    SIMPLE_STRING('+'),
    ERROR('-'),
    INTEGER(':'),
    BULK_STRING('$'),
    ARRAY('*'),
    NULL_BULK('$'),
    NULL_ARRAY('*'),
    DOUBLE(','),
    BOOLEAN('#'),
    BLOB_ERROR('!'),
    VERBATIM_STRING('='),
    BIG_NUMBER('('),
    MAP('%'),
    SET('~'),
    ATTRIBUTE('|'),
    PUSH('>');

    private final char marker;
    private final byte markerByte;

    RespType(char marker) {
        this.marker = marker;
        this.markerByte = (byte) marker;
    }

    public char getMarker() {
        return marker;
    }

    public byte getMarkerByte() {
        return markerByte;
    }

    public static RespType fromByte(byte b) {
        for (RespType t : values()) {
            if (t.markerByte == b) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown RESP marker byte: " + b);
    }
}