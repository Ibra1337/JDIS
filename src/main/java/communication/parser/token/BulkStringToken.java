package communication.parser.token;

public class BulkStringToken implements RespToken {
    private final String value;

    public BulkStringToken(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public byte getType() {
        return '$';
    }

    @Override
    public String toString() {
        return "BulkStringToken{" + "value='" + value + '\'' + '}';
    }
}
