package command.commandRes;

public class ErrorResult implements CommandResult {
    private final String message;

    public ErrorResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toRespFormat() {
        return null;
    }
}
