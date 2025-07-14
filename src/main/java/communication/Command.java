package communication;

import lombok.Getter;

import java.util.Collection;
import java.util.List;

@Getter
public class Command {

    @Override
    public String toString() {
        return "Command{" +
                "command='" + name + '\'' +
                ", args=" + args +
                '}';
    }

    private String name;
    private List<String> args;

    public Command(String command, List<String> args) {
        this.name = command;
        this.args = args;
    }

}
