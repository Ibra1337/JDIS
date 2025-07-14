package command.commandRes;

public class BulkStringResult implements CommandResult {
    private final String value; // or byte[]

    public BulkStringResult(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toRespFormat() {
        return null;
    }
}
