package communication.parser.token;

public class IntegerToken implements RespToken {
    private final long value;

    public IntegerToken(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public byte getType() {
        return ':';
    }

    @Override
    public String toString() {
        return "IntegerToken{" + "value=" + value + '}';
    }
}
