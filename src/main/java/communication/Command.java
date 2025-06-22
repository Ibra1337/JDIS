package communication;

import java.util.Collection;

public class Command {

    @Override
    public String toString() {
        return "Command{" +
                "command='" + command + '\'' +
                ", args=" + args +
                '}';
    }

    private String command;
    private  Collection<String> args;

    public Command(String command, Collection<String> args) {
        this.command = command;
        this.args = args;
    }

    public String getCommand() {
        return command;
    }

    public Collection<String> getArgs() {
        return args;
    }
}
