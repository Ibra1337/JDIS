package command.commandRes;

public class NullBulkStringResult implements CommandResult {
    @Override
    public String toRespFormat() {
        return "$-1\r\n";
    }
}
