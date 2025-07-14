package communication.parser.token;

public class SimpleStringToken implements RespToken {
    private final String value;

    public SimpleStringToken(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public byte getType() {
        return '+';
    }

    @Override
    public String toString() {
        return "SimpleStringToken{" + "value='" + value + '\'' + '}';
    }
}
