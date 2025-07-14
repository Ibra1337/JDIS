package communication.parser.token;

import java.util.List;

public class ArrayToken implements RespToken {
    private final List<RespToken> elements;

    public ArrayToken(List<RespToken> elements) {
        this.elements = elements;
    }

    public List<RespToken> getElements() {
        return elements;
    }

    @Override
    public byte getType() {
        return '*';
    }

    @Override
    public String toString() {
        return "ArrayToken{" + "elements=" + elements + '}';
    }
}
