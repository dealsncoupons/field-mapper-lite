package works.hop.hydrate.gen.core;

public class Token {

    int start;
    String value;
    String type;

    public Token(int start, String value, String type) {
        this.start = start;
        this.value = value;
        this.type = type;
    }
}
