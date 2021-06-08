package works.hop.hydrate.gen.core;

import works.hop.hydrate.gen.builder.*;
import works.hop.hydrate.gen.builder.*;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class Parser {

    final List<Token> tokens = new ArrayList<>();
    final Stack<TypeBuilder<?>> builderStack = new Stack<>();
    final List<Node> readyList = new ArrayList<>();
    String namespace;
    int current = 0;

    public Parser(List<Token> tokens) {
        if (tokens.isEmpty()) {
            throw new RuntimeException("Parser cannot function without tokens");
        }
        this.tokens.addAll(tokens);
    }

    public static void main(String[] args) {
        String defaultSrcDir = "sample-hydrate-app-1/src/main/resources/avro";
//        String defaultDestDir = "hydrate-entity-jdbc/build/generated-sources";
        String defaultDestDir = "sample-hydrate-app-1/src/main/java";
        String srcDir = args != null && args.length > 0 ? args[0] : defaultSrcDir;
        String destDir = args != null && args.length > 0 ? args[0] : defaultDestDir;
        Parser.generateJavro(srcDir, destDir);
    }

    public static void generateJavro(String srcDir, String destDir) {
        generateJavroUsingDir(Paths.get(srcDir).toFile(), Paths.get(destDir).toFile());
    }

    public static void generateJavroUsingDir(File srcDir, File destDir) {
        File[] listOfFiles = srcDir.listFiles((dir, name) -> name.endsWith(".avsc"));
        if (listOfFiles != null) {
            Progress progress = new Progress(destDir);
            for (File sourceFile : listOfFiles) {
                generateJavroUsingFile(sourceFile, destDir, progress);
            }
            progress.complete();
        } else {
            System.err.println("Could not find files from specified directory - '" + srcDir + "'");
        }
    }

    public static void generateJavroUsingFile(File sourceFile, File outputDir, Progress progress) {
        Lexer gen = new Lexer(sourceFile);
        gen.parse();

        Parser parser = new Parser(gen.getTokens());
        parser.parse();

        List<String> extraTypes = parser.getReadyList().stream().map(node ->
                parser.qualifiedTypeName(node.packageName, node.name)).collect(Collectors.toList());

        for (Node node : parser.getReadyList()) {
            Generator generator = new Generator(node, extraTypes, outputDir);
            generator.listener(progress);
            generator.generate();
        }
    }

    public List<Node> getReadyList() {
        return readyList;
    }

    public String qualifiedTypeName(String namespace, String typeName) {
        if (namespace != null) {
            if (typeName != null) {
                return String.format("%s.%s", namespace, typeName);
            }
        } else {
            if (typeName != null) {
                return typeName;
            }
        }
        throw new RuntimeException("Expected a type name to derive the qualified type name");
    }

    public void parse() {
        //precondition - must have at least 3 tokens
        if (tokens.size() < 3) {
            throw new RuntimeException("These no sufficient tokens to parse");
        }
        List<String> permissible = List.of(TokenType.RECORD, TokenType.ENUM);
        int index = 1;
        Token token = tokens.get(index);
        while (!permissible.contains(token.value)) {
            token = tokens.get(++index);
        }
        switch (token.value) {
            case TokenType.RECORD:
                recordDefinition();
                this.readyList.add((Node) builderStack.pop().build());
                break;
            case TokenType.ENUM:
                enumDefinition();
                this.readyList.add((Node) builderStack.pop().build());
                break;
            default:
                throw new RuntimeException("Cannot parse unknown type");
        }
        //apply missing namespace if one is defined
        this.readyList.forEach(node -> {
            if (node.packageName == null) {
                node.packageName = namespace;
            }
        });
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
            if (tokens.get(current).type.equals(TokenType.ATTRIBUTE_SEP)) {
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
        while (!tokens.get(current).type.equals(TokenType.END_ARRAY)) {
            string();
            unionTypeBuilder.add(tokens.get(current).value);
            current++;
            if (tokens.get(current).type.equals(TokenType.ATTRIBUTE_SEP)) {
                attributeSep();
                current++;
            }
        }
        endArray();
    }

    public void arrayDefinition() {
        startObject();
        current++;
        typeAttribute();
        current++;
        keyValueSep();
        current++;
        arrayType();
        ArrayTypeBuilder arrayBuilder = new ArrayTypeBuilder();
        builderStack.add(arrayBuilder);
        arrayBuilder.type(tokens.get(current).type);
        current++;
        attributeSep();
        current++;
        itemsProperty();
        endObject();
    }

    public void mapDefinition() {
        startObject();
        current++;
        typeAttribute();
        current++;
        keyValueSep();
        current++;
        mapType();
        MapTypeBuilder mapTypeBuilder = new MapTypeBuilder();
        builderStack.add(mapTypeBuilder);
        mapTypeBuilder.type(tokens.get(current).type);
        current++;
        attributeSep();
        current++;
        valuesProperty();
        endObject();
    }

    public void enumDefinition() {
        startObject();
        current++;

        EnumTypeBuilder enumBuilder = new EnumTypeBuilder();
        builderStack.add(enumBuilder);

        List<String> acceptable = List.of(TokenType.TYPE, TokenType.NAME, TokenType.SYMBOLS);
        Token token = tokens.get(current);
        while (acceptable.contains(token.type)) {
            switch (token.type) {
                case TokenType.TYPE:
                    typeAttribute();
                    current++;
                    keyValueSep();
                    current++;
                    enumType();
                    enumBuilder.type(tokens.get(current).value);
                    current++;
                    break;
                case TokenType.NAME:
                    nameAttribute();
                    current++;
                    keyValueSep();
                    current++;
                    string();
                    enumBuilder.name(tokens.get(current).value);
                    current++;
                    break;
                case TokenType.SYMBOLS:
                    symbolsProperty();
                    current++;
                    break;
                default:
                    break;
            }
            if (tokens.get(current).type.equals(TokenType.ATTRIBUTE_SEP)) {
                attributeSep();
                current++;
            }
            token = tokens.get(current);
        }
        endObject();
    }

    public void typeProperty() {
        typeAttribute();
        current++;
        keyValueSep();
        current++;
        Token next = tokens.get(current);
        if (next.type.equals(TokenType.START_ARRAY)) {
            unionDefinition();
            UnionTypeBuilder unionTypes = (UnionTypeBuilder) builderStack.pop();
            builderStack.peek().type(unionTypes.build().type);
        } else if (next.type.equals(TokenType.START_OBJECT)) {
            //figure out whether to handle 'record', 'array' or 'map' type
            List<String> permissible = List.of(TokenType.RECORD, TokenType.ARRAY, TokenType.MAP, TokenType.ENUM);
            int index = current;
            while (index < tokens.size() && !permissible.contains(tokens.get(index).value)) {
                index++;
            }
            //type token found, so get the value
            String typeToHandle = tokens.get(index).value;

            switch (typeToHandle) {
                case TokenType.RECORD:
                    recordDefinition();
                    RecordTypeBuilder recordBuilder = (RecordTypeBuilder) builderStack.pop();
                    Node recordNode = recordBuilder.build();
                    String recordType = qualifiedTypeName(recordNode.packageName != null ? recordNode.packageName : namespace, recordNode.name);
                    builderStack.peek().type(recordType);
                    this.readyList.add(recordNode);
                    break;
                case TokenType.ENUM:
                    enumDefinition();
                    EnumTypeBuilder enumBuilder = (EnumTypeBuilder) builderStack.pop();
                    Node enumNode = enumBuilder.build();
                    String enumType = qualifiedTypeName(enumNode.packageName != null ? enumNode.packageName : namespace, enumNode.name);
                    builderStack.peek().type(enumType);
                    this.readyList.add(enumNode);
                    break;
                case TokenType.MAP:
                    mapDefinition();
                    MapTypeBuilder mapBuilder = (MapTypeBuilder) builderStack.pop();
                    Node mapNode = mapBuilder.build();
                    builderStack.peek().type(mapNode.name);
                    builderStack.peek().values(mapNode.values);
                    break;
                case TokenType.ARRAY:
                    arrayDefinition();
                    ArrayTypeBuilder arrayBuilder = (ArrayTypeBuilder) builderStack.pop();
                    Node arrayNode = arrayBuilder.build();
                    builderStack.peek().type(arrayNode.name);
                    builderStack.peek().items(arrayNode.items);
                    break;
                default:
                    throw new RuntimeException("Encountered type '" + typeToHandle + "' which is not handled (yet)");
            }
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
        while (tokens.get(current).type.equals(TokenType.ATTRIBUTE_SEP)) {
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
        if (tokens.get(current).type.equals(TokenType.START_OBJECT)) {
            recordDefinition();
            RecordTypeBuilder recordBuilder = (RecordTypeBuilder) builderStack.pop();
            Node record = recordBuilder.build();
            String recordType = qualifiedTypeName(record.packageName != null ? record.packageName : namespace, record.name);
            builderStack.peek().items(recordType);
            this.readyList.add(record);
        } else {
            string();
            builderStack.peek().items(tokens.get(current).value);
        }
        current++;
        if (tokens.get(current).type.equals(TokenType.ATTRIBUTE_SEP)) {
            attributeSep();
            current++;
        }
    }

    public void valuesProperty() {
        valuesAttribute();
        current++;
        keyValueSep();
        current++;
        if (tokens.get(current).type.equals(TokenType.START_OBJECT)) {
            recordDefinition();
            RecordTypeBuilder recordBuilder = (RecordTypeBuilder) builderStack.pop();
            Node record = recordBuilder.build();
            String recordType = qualifiedTypeName(record.packageName != null ? record.packageName : namespace, record.name);
            builderStack.peek().values(recordType);
            this.readyList.add(record);
        } else {
            string();
            builderStack.peek().values(tokens.get(current).value);
        }
        current++;
        if (tokens.get(current).type.equals(TokenType.ATTRIBUTE_SEP)) {
            attributeSep();
            current++;
        }
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
        boolean hasNext = tokens.get(current).type.equals(TokenType.START_OBJECT);
        while (hasNext) {
            fieldDefinition();
            FieldTypeBuilder fieldTypeBuilder = (FieldTypeBuilder) builderStack.pop();
            builderStack.peek().add(fieldTypeBuilder.build());
            current++;
            boolean hasMore = tokens.get(current).type.equals(TokenType.ATTRIBUTE_SEP);
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

        List<String> acceptable = List.of(TokenType.TYPE, TokenType.NAME, TokenType.ANNOTATIONS, TokenType.DOC);
        Token token = tokens.get(current);
        while (acceptable.contains(token.type)) {
            switch (token.type) {
                case TokenType.TYPE:
                    typeProperty();
                    current++;
                    break;
                case TokenType.NAME:
                    nameProperty();
                    fieldTypeBuilder.name(tokens.get(current).value);
                    current++;
                    break;
                case TokenType.ANNOTATIONS:
                    annotationsProperty();
                    current++;
                    break;
                case TokenType.DOC:
                    docProperty();
                    current++;
                    break;
                default:
                    break;
            }
            if (tokens.get(current).type.equals(TokenType.ATTRIBUTE_SEP)) {
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

        List<String> acceptable = List.of(TokenType.TYPE, TokenType.NAMESPACE, TokenType.NAME, TokenType.ALIASES, TokenType.FIELDS, TokenType.ANNOTATIONS, TokenType.DOC);
        Token token = tokens.get(current);
        while (acceptable.contains(token.type)) {
            switch (token.type) {
                case TokenType.TYPE:
                    typeAttribute();
                    current++;
                    keyValueSep();
                    current++;
                    recordType();
                    recordBuilder.type(tokens.get(current).value);
                    current++;
                    break;
                case TokenType.NAMESPACE:
                    namespaceProperty();
                    recordBuilder.namespace(tokens.get(current).value);
                    this.namespace = tokens.get(current).value;
                    current++;
                    break;
                case TokenType.ALIASES:
                    aliasesProperty();
                    current++;
                    break;
                case TokenType.ANNOTATIONS:
                    annotationsProperty();
                    current++;
                    break;
                case TokenType.FIELDS:
                    fieldsProperty();
                    current++;
                    break;
                case TokenType.DOC:
                    docProperty();
                    current++;
                    break;
                case TokenType.NAME:
                    nameProperty();
                    recordBuilder.name(tokens.get(current).value);
                    current++;
                    break;
                default:
                    break;
            }
            if (tokens.get(current).type.equals(TokenType.ATTRIBUTE_SEP)) {
                attributeSep();
                current++;
            }
            token = tokens.get(current);
        }
        endObject();
    }
}
