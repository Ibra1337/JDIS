package command.commandRes;

public class BulkStringResult implements CommandResult {
    private final String value;

    public BulkStringResult(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toRespFormat() {
        if (value == null) {
            return "$-1\r\n";
        }
        return "$" + value.length() + "\r\n" + value + "\r\n";
    }
}
