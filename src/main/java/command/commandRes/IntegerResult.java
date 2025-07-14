package command.commandRes;

public class IntegerResult implements CommandResult {
    private final long value;

    public IntegerResult(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toRespFormat() {
        return null;
    }
}
