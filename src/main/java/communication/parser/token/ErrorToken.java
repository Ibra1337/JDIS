package communication.parser.token;

public class ErrorToken implements RespToken {
    private final String message;

    public ErrorToken(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public byte getType() {
        return '-';
    }

    @Override
    public String toString() {
        return "ErrorToken{" + "message='" + message + '\'' + '}';
    }
}
