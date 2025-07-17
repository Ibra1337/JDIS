package command.commandRes;

public class SimpleStringResult implements CommandResult {
    private final String value;

    public SimpleStringResult(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toRespFormat() {
        return "+" + value + "\r\n";
    }
}
