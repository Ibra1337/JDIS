package command.commandRes;

import java.util.List;

public class ArrayResult implements CommandResult {
    private final List<CommandResult> elements;

    public ArrayResult(List<CommandResult> elements) {
        this.elements = elements;
    }

    public List<CommandResult> getElements() {
        return elements;
    }

    @Override
    public String toRespFormat() {
        return null;
    }
}
