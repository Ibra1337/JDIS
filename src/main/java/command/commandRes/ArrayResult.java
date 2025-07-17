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
        StringBuilder sb = new StringBuilder();
        if (elements == null) {
            sb.append("*-1\r\n");
        } else {
            sb.append("*").append(elements.size()).append("\r\n");
            for (CommandResult elem : elements) {
                sb.append(elem.toRespFormat());
            }
        }
        return sb.toString();
    }
}
