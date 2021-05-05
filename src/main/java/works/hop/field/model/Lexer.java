package works.hop.field.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static works.hop.field.model.TokenType.*;

public class Lexer {

    final static Logger log = LoggerFactory.getLogger(Lexer.class);
    final String input;
    int current = 0;
    int lineNum = 1;
    Stack<Character> objectDepth = new Stack<>();
    Stack<Character> arrayDepth = new Stack<>();
    Action action = Action.SEEK_NEXT;
    List<Token> tokens = new ArrayList<>();
    Map<String, String> keywords = new HashMap<>() {
        {
            put(NAMESPACE, "package");
            put(DOC, "documentation");
            put(FIELDS, "attributes");
            put(ALIASES, "aliases");
            put(DEFAULT, "default");
            put(VALUES, "values");
            put(SYMBOLS, "enumeration");
            put(LOGICAL_TYPE, "logicalType");
            put(PRECISION, "precision");
            put(SCALE, "scale");
            put(TYPE, "type");
            put(NULL, "null");
            put(BOOLEAN, "boolean");
            put(INT, "int");
            put(LONG, "long");
            put(FLOAT, "float");
            put(DOUBLE, "double");
            put(BYTES, "bytes");
            put(STRING, "string");
            put(RECORD, "record");
            put(ENUM, "enum");
            put(ARRAY, "array");
            put(MAP, "map");
            put(FIXED, "fixed");
        }
    };

    public Lexer(String file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(file))))) {
            StringBuilder lines = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.append(line).append("\n");
            }
            this.input = lines.toString();
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Lexer gen = new Lexer("/model/user.avsc");
        gen.parse();
        System.out.println(gen.tokens);
    }

    public void parse() {
        while (!isEOF()) {
            char ch = input.charAt(current);
            switch (ch) {
                case '{':
                    startObject(ch);
                    break;
                case '}':
                    endObject();
                    break;
                case '[':
                    startArray(ch);
                    break;
                case ']':
                    endArray();
                    break;
                case '\"':
                    startString();
                    break;
                case ':':
                    keyValueSeparator();
                    break;
                case ',':
                    attributeSeparator();
                    break;
                case '\n':
                case '\r':
                    lineNum += 1;
                    break;
                case ' ':
                default:
                    break;
            }
            current++;
        }
        if (!objectDepth.isEmpty()) {
            log.warn("An object is missing a closing tag");
        }
        if (!arrayDepth.isEmpty()) {
            log.warn("An array is missing a closing tag");
        }
    }

    public void keyValueSeparator() {
        action = Action.READ_VALUE;
        tokens.add(new Token(current, ":", KEY_VALUE_SEP));
    }

    public void attributeSeparator() {
        tokens.add(new Token(current, ",", ATTRIBUTE_SEP));
    }

    public boolean matches(char ch) {
        return input.charAt(current) == ch;
    }

    public boolean matches(char[] chars) {
        for (char ch : chars) {
            if (ch == input.charAt(current)) {
                return true;
            }
        }
        return false;
    }

    public void startObject(char ch) {
        objectDepth.add(ch);
        action = Action.READ_PROPERTY;
        tokens.add(new Token(current, "{", START_OBJECT));
    }

    public void endObject() {
        objectDepth.pop();
        action = Action.SEEK_NEXT;
        tokens.add(new Token(current, "}", END_OBJECT));
    }

    public void startArray(char ch) {
        arrayDepth.add(ch);
        tokens.add(new Token(current, "[", START_ARRAY));
    }

    public void endArray() {
        arrayDepth.pop();
        tokens.add(new Token(current, "]", END_ARRAY));
    }

    public void startString() {
        int start = ++current;
        while (!isEOF() && !matches('\"')) {
            current++;
        }
        String value = input.substring(start, current);
        Token reserved = reserved(start, value);
        tokens.add(Objects.requireNonNullElseGet(reserved, () ->
                new Token(start, input.substring(start, current), action == Action.READ_PROPERTY ? FIELD_NAME : STRING)));
        action = Action.SEEK_NEXT;
    }

    public Token reserved(int start, String string) {
        if (action == Action.READ_PROPERTY && keywords.containsKey(string)) {
            return new Token(start, string, keywords.get(string));
        }
        return null;
    }

    public boolean isEOF() {
        return current >= input.length();
    }

    enum Action {SEEK_NEXT, READ_PROPERTY, READ_VALUE}

}
