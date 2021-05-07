package works.hop.field.model;

import works.hop.field.model.builder.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static works.hop.field.model.TokenType.*;

public class Parser {

    final List<Token> tokens = new ArrayList<>();
    final Stack<TypeBuilder<?>> builderStack = new Stack<>();
    final List<String> primitiveTypes = List.of("null", "boolean", "int", "long", "float", "double", "bytes", "string");
    final List<String> complexTypes = List.of("record", "array", "enum", "map", "fixed");
    final List<Node> nodesBuilt = new ArrayList<>();
    final List<String> generatedTypes = new ArrayList<>();
    int current = 0;

    public Parser(List<Token> tokens) {
        if (tokens.isEmpty()) {
            throw new RuntimeException("Parser cannot function without tokens");
        }
        this.tokens.addAll(tokens);
    }

    public static void main(String[] args) {
        Lexer gen = new Lexer("/model/account.avsc");
        gen.parse();

        Parser parser = new Parser(gen.getTokens());
        parser.parse();

        Generator generator = new Generator(parser.getNodesBuilt().get(0));
        generator.generate();
    }

    public List<Node> getNodesBuilt() {
        return nodesBuilt;
    }

    public void parse() {
        //precondition - must have at least 3 tokens
        if (tokens.size() < 3) {
            throw new RuntimeException("These no sufficient tokens to parse");
        }
        List<String> permissible = List.of(RECORD, ENUM);
        int index = 1;
        Token token = tokens.get(index);
        while (!permissible.contains(token.value)) {
            token = tokens.get(++index);
        }
        switch (token.value) {
            case RECORD:
                recordDefinition();
                break;
            case ENUM:
                enumDefinition();
                break;
            default:
                throw new RuntimeException("Cannot parse unknown type");
        }
    }

    public void nullType() {
        Token token = tokens.get(current);
        if (!token.type.equals("null")) {
            throw new RuntimeException("Expected null type by found " + token.type);
        }
    }

    public void booleanType() {
        Token token = tokens.get(current);
        if (!token.type.equals("boolean")) {
            throw new RuntimeException("Expected boolean type by found " + token.type);
        }
    }

    public void intType() {
        Token token = tokens.get(current);
        if (!token.type.equals("int")) {
            throw new RuntimeException("Expected int type by found " + token.type);
        }
    }

    public void longType() {
        Token token = tokens.get(current);
        if (!token.type.equals("long")) {
            throw new RuntimeException("Expected long type by found " + token.type);
        }
    }

    public void floatType() {
        Token token = tokens.get(current);
        if (!token.type.equals("float")) {
            throw new RuntimeException("Expected float type by found " + token.type);
        }
    }

    public void doubleType() {
        Token token = tokens.get(current);
        if (!token.type.equals("double")) {
            throw new RuntimeException("Expected double type by found " + token.type);
        }
    }

    public void bytesType() {
        Token token = tokens.get(current);
        if (!token.type.equals("bytes")) {
            throw new RuntimeException("Expected bytes type by found " + token.type);
        }
    }

    public void stringType() {
        Token token = tokens.get(current);
        if (!token.type.equals("string")) {
            throw new RuntimeException("Expected string type by found " + token.type);
        }
    }

    public void recordType() {
        Token token = tokens.get(current);
        if (!token.value.equals("record")) {
            throw new RuntimeException("Expected record type by found " + token.type);
        }
    }

    public void enumType() {
        Token token = tokens.get(current);
        if (!token.value.equals("enum")) {
            throw new RuntimeException("Expected enum type by found " + token.type);
        }
    }

    public void arrayType() {
        Token token = tokens.get(current);
        if (!token.value.equals("array")) {
            throw new RuntimeException("Expected array type by found " + token.type);
        }
    }

    public void mapType() {
        Token token = tokens.get(current);
        if (!token.value.equals("map")) {
            throw new RuntimeException("Expected map type by found " + token.type);
        }
    }

    public void fixedType() {
        Token token = tokens.get(current);
        if (!token.type.equals("fixed")) {
            throw new RuntimeException("Expected fixed type by found " + token.type);
        }
    }

    public void primitiveType() {
        Token token = tokens.get(current);
        if (!primitiveTypes.contains(token.type)) {
            throw new RuntimeException("Expected a primitive type by found " + token.type);
        }
    }

    public void complexType() {
        Token token = tokens.get(current);
        if (!complexTypes.contains(token.type)) {
            throw new RuntimeException("Expected a complex type by found " + token.type);
        }
    }

    public void typeAttribute() {
        Token token = tokens.get(current);
        if (!token.value.equals("type")) {
            throw new RuntimeException("Expected a type attribute by found " + token.type);
        }
    }

    public void symbolsAttribute() {
        Token token = tokens.get(current);
        if (!token.value.equals("symbols")) {
            throw new RuntimeException("Expected a symbols attribute by found " + token.type);
        }
    }

    public void itemsAttribute() {
        Token token = tokens.get(current);
        if (!token.value.equals("items")) {
            throw new RuntimeException("Expected a items attribute by found " + token.type);
        }
    }

    public void nameAttribute() {
        Token token = tokens.get(current);
        if (!token.value.equals("name")) {
            throw new RuntimeException("Expected a name attribute by found " + token.type);
        }
    }

    public void namespaceAttribute() {
        Token token = tokens.get(current);
        if (!token.value.equals("namespace")) {
            throw new RuntimeException("Expected a namespace attribute by found " + token.type);
        }
    }

    public void annotationsAttribute() {
        Token token = tokens.get(current);
        if (!token.value.equals("annotations")) {
            throw new RuntimeException("Expected an annotations attribute by found " + token.type);
        }
    }

    public void sizeAttribute() {
        Token token = tokens.get(current);
        if (!token.value.equals("size")) {
            throw new RuntimeException("Expected a size attribute by found " + token.type);
        }
    }

    public void valuesAttribute() {
        Token token = tokens.get(current);
        if (!token.value.equals("values")) {
            throw new RuntimeException("Expected a values attribute by found " + token.type);
        }
    }

    public void aliasesAttribute() {
        Token token = tokens.get(current);
        if (!token.value.equals("aliases")) {
            throw new RuntimeException("Expected an aliases attribute by found " + token.type);
        }
    }

    public void fieldsAttribute() {
        Token token = tokens.get(current);
        if (!token.value.equals("fields")) {
            throw new RuntimeException("Expected a fields attribute by found " + token.type);
        }
    }

    public void defaultAttribute() {
        Token token = tokens.get(current);
        if (!token.value.equals("default")) {
            throw new RuntimeException("Expected a default attribute by found " + token.type);
        }
    }

    public void docAttribute() {
        Token token = tokens.get(current);
        if (!token.value.equals("doc")) {
            throw new RuntimeException("Expected a doc attribute by found " + token.type);
        }
    }

    public void keyValueSep() {
        Token token = tokens.get(current);
        if (!token.value.equals(":")) {
            throw new RuntimeException("Expected a colon by found " + token.value);
        }
    }

    public void attributeSep() {
        Token token = tokens.get(current);
        if (!token.value.equals(",")) {
            throw new RuntimeException("Expected a comma by found " + token.value);
        }
    }

    public void startObject() {
        Token token = tokens.get(current);
        if (!token.value.equals("{")) {
            throw new RuntimeException("Expected a '{' by found " + token.value);
        }
    }

    public void endObject() {
        Token token = tokens.get(current);
        if (!token.value.equals("}")) {
            throw new RuntimeException("Expected a '}' by found " + token.value);
        }
    }

    public void startArray() {
        Token token = tokens.get(current);
        if (!"[".equals(token.value)) {
            throw new RuntimeException("Expected a '[' by found " + token.value);
        }
    }

    public void endArray() {
        Token token = tokens.get(current);
        if (!"]".equals(token.value)) {
            throw new RuntimeException("Expected a ']' by found " + token.value);
        }
    }

    public boolean string() {
        return tokens.get(current).type.equals("string");
    }

    public void symbolsProperty() {
        symbolsAttribute();
        current++;
        keyValueSep();
        current++;
        startArray();
        current++;
        while (string()) {
            String enumValue = tokens.get(current).value;
            builderStack.peek().add(enumValue);
            current++;
            if (tokens.get(current).type.equals(ATTRIBUTE_SEP)) {
                attributeSep();
                current++;
            }
        }
        endArray();
    }

    public void unionDefinition() {
        startArray();
        UnionTypeBuilder unionTypeBuilder = new UnionTypeBuilder();
        builderStack.add(unionTypeBuilder);
        current++;
        while (!tokens.get(current).type.equals(END_ARRAY)) {
            string();
            unionTypeBuilder.add(tokens.get(current).value);
            current++;
            if (tokens.get(current).type.equals(ATTRIBUTE_SEP)) {
                attributeSep();
                current++;
            }
        }
        endArray();
    }

    public void enumDefinition() {
        startObject();
        current++;

        EnumTypeBuilder enumBuilder = new EnumTypeBuilder();
        builderStack.add(enumBuilder);

        List<String> acceptable = List.of(TYPE, NAME, SYMBOLS);
        Token token = tokens.get(current);
        while (acceptable.contains(token.type)) {
            switch (token.type) {
                case TYPE:
                    typeAttribute();
                    current++;
                    keyValueSep();
                    current++;
                    enumType();
                    enumBuilder.type(tokens.get(current).value);
                    current++;
                    break;
                case NAME:
                    nameAttribute();
                    current++;
                    keyValueSep();
                    current++;
                    string();
                    enumBuilder.name(tokens.get(current).value);
                    current++;
                    break;
                case SYMBOLS:
                    symbolsProperty();
                    current++;
                    break;
                default:
                    break;
            }
            if (tokens.get(current).type.equals(ATTRIBUTE_SEP)) {
                attributeSep();
                current++;
            }
            token = tokens.get(current);
        }
        endObject();
        this.nodesBuilt.add((Node) builderStack.pop().build());
    }

    public void arrayDefinition() {
        startObject();
        current++;
        typeAttribute();
        current++;
        arrayType();
        ArrayTypeBuilder arrayBuilder = new ArrayTypeBuilder();
        builderStack.add(arrayBuilder);
        arrayBuilder.type(tokens.get(current).type);
        current++;
        attributeSep();
        itemsProperty();
        endObject();
    }

    public void typeProperty() {
        typeAttribute();
        current++;
        keyValueSep();
        current++;
        Token next = tokens.get(current);
        if (next.type.equals(START_ARRAY)) {
            unionDefinition();
            UnionTypeBuilder unionTypes = (UnionTypeBuilder) builderStack.pop();
            builderStack.peek().type(unionTypes.build().type);
        }
        else if(next.type.equals(START_OBJECT)){
            recordDefinition();
            RecordTypeBuilder recordBuilder = (RecordTypeBuilder) builderStack.pop();
            Node nestedRecord = recordBuilder.build();
            String recordType = nestedRecord.packageName + nestedRecord.type;
            builderStack.peek().type(recordType);
            this.nodesBuilt.add(nestedRecord);
        } else {
            string();
            builderStack.peek().type(tokens.get(current).value);
        }
    }

    public void annotationsProperty() {
        annotationsAttribute();
        current++;
        keyValueSep();
        current++;
        startArray();
        current++;
        string();
        builderStack.peek().annotation(tokens.get(current).value);
        current++;
        while (tokens.get(current).type.equals(ATTRIBUTE_SEP)) {
            attributeSep();
            current++;
            string();
            builderStack.peek().annotation(tokens.get(current).value);
            current++;
        }
        endArray();
    }

    public void itemsProperty() {
        itemsAttribute();
        current++;
        keyValueSep();
        current++;
        string();
        builderStack.peek().items(tokens.get(current).value);
    }

    public void nameProperty() {
        nameAttribute();
        current++;
        keyValueSep();
        current++;
        string();
    }

    public void namespaceProperty() {
        namespaceAttribute();
        current++;
        keyValueSep();
        current++;
        string();
    }

    public void aliasesProperty() {
        aliasesAttribute();
        current++;
        keyValueSep();
        current++;
        startArray();
        current++;
        string();
        current++;
        endArray();
    }

    public void docProperty() {
        docAttribute();
        current++;
        keyValueSep();
        current++;
        string();
    }

    public void fieldsProperty() {
        fieldsAttribute();
        current++;
        keyValueSep();
        current++;
        startArray();
        current++;
        boolean hasNext = tokens.get(current).type.equals(START_OBJECT);
        while (hasNext) {
            fieldDefinition();
            FieldTypeBuilder fieldTypeBuilder = (FieldTypeBuilder) builderStack.pop();
            builderStack.peek().add(fieldTypeBuilder.build());
            current++;
            boolean hasMore = tokens.get(current).type.equals(ATTRIBUTE_SEP);
            if (hasMore) {
                attributeSep();
                current++;
            } else {
                hasNext = false;
            }
        }
        endArray();
    }

    public void fieldDefinition() {
        startObject();
        current++;

        FieldTypeBuilder fieldTypeBuilder = new FieldTypeBuilder();
        builderStack.add(fieldTypeBuilder);

        List<String> acceptable = List.of(TYPE, NAME, ANNOTATIONS, DOC);
        Token token = tokens.get(current);
        while (acceptable.contains(token.type)) {
            switch (token.type) {
                case TYPE:
                    typeProperty();
                    current++;
                    break;
                case NAME:
                    nameProperty();
                    fieldTypeBuilder.name(tokens.get(current).value);
                    current++;
                    break;
                case ANNOTATIONS:
                    annotationsProperty();
                    current++;
                    break;
                default:
                    break;
            }
            if (tokens.get(current).type.equals(ATTRIBUTE_SEP)) {
                attributeSep();
                current++;
            }
            token = tokens.get(current);
        }
        endObject();
    }

    public void recordDefinition() {
        startObject();
        current++;

        RecordTypeBuilder recordBuilder = new RecordTypeBuilder();
        builderStack.add(recordBuilder);

        List<String> acceptable = List.of(TYPE, NAMESPACE, NAME, ALIASES, FIELDS, ANNOTATIONS, DOC);
        Token token = tokens.get(current);
        while (acceptable.contains(token.type)) {
            switch (token.type) {
                case TYPE:
                    typeAttribute();
                    current++;
                    keyValueSep();
                    current++;
                    recordType();
                    recordBuilder.type(tokens.get(current).value);
                    current++;
                    break;
                case NAMESPACE:
                    namespaceProperty();
                    recordBuilder.namespace(tokens.get(current).value);
                    current++;
                    break;
                case ALIASES:
                    aliasesProperty();
                    current++;
                    break;
                case ANNOTATIONS:
                    annotationsProperty();
                    current++;
                    break;
                case FIELDS:
                    generatedTypes.add(recordBuilder.qualifiedName()); //allows using this type as a field type
                    fieldsProperty();
                    current++;
                    break;
                case DOC:
                    docProperty();
                    break;
                case NAME:
                    nameProperty();
                    recordBuilder.name(tokens.get(current).value);
                    current++;
                    break;
                default:
                    break;
            }
            if (tokens.get(current).type.equals(ATTRIBUTE_SEP)) {
                attributeSep();
                current++;
            }
            token = tokens.get(current);
        }
        endObject();
        this.nodesBuilt.add((Node) builderStack.pop().build());
    }
}
