package works.hop.javro.gen.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class Lexer {

    final static Logger log = LoggerFactory.getLogger(Lexer.class);
    final String input;
    final Stack<Character> objectDepth = new Stack<>();
    final Stack<Character> arrayDepth = new Stack<>();
    final List<Token> tokens = new ArrayList<>();
    int current = 0;
    int lineNum = 1;
    Action action = Action.READ_PROPERTY;
    Map<String, String> keywords = new HashMap<>() {
        {
            put(TokenType.NAMESPACE, "namespace");
            put(TokenType.DOC, "doc");
            put(TokenType.FIELDS, "fields");
            put(TokenType.ALIASES, "aliases");
            put(TokenType.DEFAULT, "default");
            put(TokenType.VALUES, "values");
            put(TokenType.SYMBOLS, "symbols");
            put(TokenType.ANNOTATIONS, "annotations");
            put(TokenType.LOGICAL_TYPE, "logicaltype");
            put(TokenType.PRECISION, "precision");
            put(TokenType.SCALE, "scale");
            put(TokenType.TYPE, "type");
            put(TokenType.NAME, "name");
            put(TokenType.NULL, "null");
            put(TokenType.BOOLEAN, "boolean");
            put(TokenType.INT, "int");
            put(TokenType.LONG, "long");
            put(TokenType.FLOAT, "float");
            put(TokenType.DOUBLE, "double");
            put(TokenType.BYTES, "bytes");
            put(TokenType.STRING, "string");
            put(TokenType.RECORD, "record");
            put(TokenType.ENUM, "enum");
            put(TokenType.ARRAY, "array");
            put(TokenType.MAP, "map");
            put(TokenType.FIXED, "fixed");
            put(TokenType.COMMENT, "comment");
        }
    };

    public Lexer(String file) {
        String source;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(file))))) {
            source = readAllLines(reader);
        } catch (NullPointerException | IOException e) {
            try (BufferedReader reader = new BufferedReader(new FileReader(Paths.get(file).toFile()))) {
                source = readAllLines(reader);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                throw new RuntimeException(ioe);
            }
        }
        this.input = source;
    }

    public Lexer(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            this.input = readAllLines(reader);
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Lexer gen = new Lexer("/model/ex1.avsc");
        gen.parse();
        System.out.println(gen.tokens);
    }

    public String readAllLines(BufferedReader reader) throws IOException {
        StringBuilder lines = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            lines.append(line).append("\n");
        }
        return lines.toString();
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void parse() {
        while (!isEOF()) {
            char ch = input.charAt(current);
            switch (ch) {
                case '{':
                    startObject(ch);
                    action = Action.READ_PROPERTY;
                    break;
                case '}':
                    endObject();
                    action = Action.SEEK_NEXT;
                    break;
                case '[':
                    startArray(ch);
                    break;
                case ']':
                    endArray();
                    break;
                case '\"':
                    if (current > 1 && input.charAt(current - 1) != '\\') startString();
                    break;
                case ':':
                    keyValueSeparator();
                    action = Action.SEEK_NEXT;
                    break;
                case ',':
                    attributeSeparator();
                    action = Action.READ_PROPERTY;
                    break;
                case '\n':
                case '\r':
                    lineNum += 1;
                    break;
                case '/':
                    if (input.charAt(current + 1) == '/') {
                        startComment();
                    }
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

    public void startComment() {
        current++;
        while (input.charAt(current) != '\n') {
            current++;
        }
    }

    public void keyValueSeparator() {
        tokens.add(new Token(current, ":", TokenType.KEY_VALUE_SEP));
    }

    public void attributeSeparator() {
        tokens.add(new Token(current, ",", TokenType.ATTRIBUTE_SEP));
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
        tokens.add(new Token(current, "{", TokenType.START_OBJECT));
    }

    public void endObject() {
        objectDepth.pop();
        action = Action.SEEK_NEXT;
        tokens.add(new Token(current, "}", TokenType.END_OBJECT));
    }

    public void startArray(char ch) {
        arrayDepth.add(ch);
        tokens.add(new Token(current, "[", TokenType.START_ARRAY));
    }

    public void endArray() {
        arrayDepth.pop();
        tokens.add(new Token(current, "]", TokenType.END_ARRAY));
    }

    public void startString() {
        int start = ++current;
        while (!isEOF() && (!matches('\"') || input.charAt(current - 1) == '\\')) {
            current++;
        }
        String value = input.substring(start, current);
        Token reserved = reserved(start, value);
        tokens.add(Objects.requireNonNullElseGet(reserved,
                () -> new Token(start, input.substring(start, current), TokenType.STRING)));
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

    enum Action {SEEK_NEXT, READ_PROPERTY}

}
