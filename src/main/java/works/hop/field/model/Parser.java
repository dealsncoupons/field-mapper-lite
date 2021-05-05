package works.hop.field.model;

import works.hop.field.model.builder.ArrayTypeBuilder;
import works.hop.field.model.builder.EnumTypeBuilder;
import works.hop.field.model.builder.FieldTypeBuilder;
import works.hop.field.model.builder.RecordTypeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static works.hop.field.model.TokenType.*;

//  primitiveType() -> nullType() | booleanType() | intType() | longType() | floatType() | doubleType() | bytesType() | stringType()
//  complexType()   -> recordType() | enumType() | arrayType () | mapType() | fixedType()
//  nullType()      -> "null"
//  booleanType()   -> "boolean"
//  intType()       -> "int"
//  longType()      -> "long"
//  floatType()     -> "float"
//  doubleType()    -> "double"
//  bytesType()     -> "bytes"
//  stringType()    -> "string"
//  recordType()    -> "record"
//  enumType()    -> "enum"
//  arrayType()    -> "array"
//  mapType()    -> "map"
//  fixedType()    -> "fixed"
//  typeAttribute()  -> "type"
//  symbolsAttribute()  -> "symbols"
//  itemsAttribute()    -> "items"
//  nameAttribute()    -> "name"
//  sizeAttribute()    -> "size"
//  valuesAttribute()    -> "values"
//  aliasesAttribute()  -> "aliases"
//  fieldsAttribute()   -> "fields"
//  defaultAttribute()   -> "default"
//  docAttribute()   -> "doc"
//  namespaceAttribute()   -> "namespace"
//  keyValueSep()   -> ":"
//  attributeSep()  -> ","
//  startObject()  -> "{"
//  endObject()  -> "}"
//  startArray()  -> "["
//  endArray()  -> "]"
//  fieldName()     -> string()
//  symbolsProperty()   -> symbolsAttribute() keyValueSep() startArray() string()+ endArray()
//  docProperty()   -> docAttribute()  keyValueSep() string()
//  fieldDefinition() -> startObject() nameProperty(), attributeSep(), typeProperty() endObject()
//  fieldsProperty() -> fieldsAttribute() keyValueSep() startArray() fieldDefinition()+, endArray()
//  typeProperty()  -> typeAttribute() keyValueSep() (primitiveType() | complexType() | unionDefinition())
//  nameProperty()  -> nameAttribute() keyValueSep() fieldName()
//  sizeProperty()  -> sizeAttribute() keyValueSep() int()
//  itemsProperty() -> itemsAttribute() keyValueSep() ()string()
//  defaultListProperty()   -> defaultAttribute() keyValueSep() startArray() string()* endArray()
//  defaultMapProperty()   -> defaultAttribute() keyValueSep() startObject()  endObject()
//  aliasesProperty()   -> aliasesAttribute() keyValueSep() startArray() string()+ endArray()
//  recordDefinition()    -> startObject() typeProperty() attributeSep() nameProperty()
//                  -> [attributeSep() docProperty()] [attributeSep() aliasesProperty()]
//                  -> attributeSep() fieldsProperty()
//                  -> endObject()
//  enumDefinition()    -> startObject() typeAttribute() attributeSep() nameProperty() symbolsProperty()
//                      endObject()
//  arrayDefinition()     -> startObject() typeAttribute() attributeSep() arrayType()
//                      attributeSep() itemsProperty() [attributeSep() defaultListProperty()] endObject()
//  mapDefinition()     -> startObject() typeProperty() attributeSep() valuesProperty() [attributeSep() defaultMapProperty()] endObject()
//  unionDefinition()     -> startArray() string()+ endArray()
//  fixedDefinition()   -> startObject() typeProperty() attributeSep() sizeProperty() attributeSep() nameProperty() endObject()

public class Parser {

    final List<Token> tokens = new ArrayList<>();
    final Stack<TypeBuilder<?>> builderStack = new Stack<>();
    final List<String> primitiveTypes = List.of("null", "boolean", "int", "long", "float", "double", "bytes", "string");
    final List<String> complexTypes = List.of("record", "array", "enum", "map", "fixed");
    final List<Node> nodesBuilt = new ArrayList<>();
    int current = 0;

    public Parser(List<Token> tokens) {
        if (tokens.isEmpty()) {
            throw new RuntimeException("Parser cannot function without tokens");
        }
        this.tokens.addAll(tokens);
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
        //symbolsAttribute() keyValueSep() startArray() string()+ endArray()
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
        //startArray() string()+ endArray()
        startArray();
        current++;
        while (!tokens.get(current).type.equals(END_ARRAY)) {
            string();
            current++;
        }
        endArray();
    }

    public void enumDefinition() {
        //startObject() typeAttribute() attributeSep() nameProperty() symbolsProperty() endObject()
        startObject();
        current++;

        EnumTypeBuilder enumBuilder = new EnumTypeBuilder();
        builderStack.add(enumBuilder);
        enumBuilder.type(tokens.get(current).type);

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
    }

    public void arrayDefinition() {
        //startObject() typeAttribute() attributeSep() arrayType()
        //attributeSep() itemsProperty() [attributeSep() defaultListProperty()] endObject()
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
        //typeAttribute() keyValueSep() ()string()
        typeAttribute();
        current++;
        keyValueSep();
        current++;
        Token next = tokens.get(current);
        if (next.type.equals(START_ARRAY)) {
            unionDefinition();
        } else {
            string();
        }
        builderStack.peek().type(tokens.get(current).value);
    }

    public void itemsProperty() {
        //itemsAttribute() keyValueSep() ()string()
        itemsAttribute();
        current++;
        keyValueSep();
        current++;
        string();
        builderStack.peek().items(tokens.get(current).value);
    }

    public void nameProperty() {
        //nameAttribute() keyValueSep() ()string()
        nameAttribute();
        current++;
        keyValueSep();
        current++;
        string();
    }

    public void namespaceProperty() {
        //namespaceAttribute() keyValueSep() ()string()
        namespaceAttribute();
        current++;
        keyValueSep();
        current++;
        string();
    }

    public void aliasesProperty() {
        //aliasesAttribute() keyValueSep() startArray() string()+ endArray()
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
        //docAttribute()  keyValueSep() string()
        docAttribute();
        current++;
        keyValueSep();
        current++;
        string();
    }

    public void fieldDefinition() {
        //startObject() nameProperty(), attributeSep(), typeProperty() endObject()
        startObject();
        current++;
        nameProperty();
        FieldTypeBuilder fieldBuilder = new FieldTypeBuilder();
        builderStack.add(fieldBuilder);
        fieldBuilder.name(tokens.get(current).value);
        current++;
        attributeSep();
        current++;
        typeProperty();
        fieldBuilder.type(tokens.get(current).value);
        current++;
        endObject();
    }

    public void fieldsProperty() {
        //fieldsAttribute() keyValueSep() startArray() fieldDefinition()+, endArray()
        fieldsAttribute();
        current++;
        keyValueSep();
        current++;
        startArray();
        current++;
        boolean parseFields = tokens.get(current).type.equals(START_OBJECT);
        while (parseFields) {
            fieldDefinition();
            FieldTypeBuilder fieldTypeBuilder = (FieldTypeBuilder) builderStack.pop();
            builderStack.peek().add(fieldTypeBuilder.build());
            current++;
            boolean hasMore = tokens.get(current).type.equals(ATTRIBUTE_SEP);
            if (hasMore) {
                attributeSep();
                current++;
            } else {
                parseFields = false;
            }
        }
        endArray();
    }

    public void recordDefinition() {
        //startObject() typeProperty() attributeSep() nameProperty()
        //  -> [attributeSep() docProperty()] [attributeSep() aliasesProperty()]
        //  -> attributeSep() fieldsProperty() endObject()
        startObject();
        RecordTypeBuilder recordBuilder = new RecordTypeBuilder();
        builderStack.add(recordBuilder);
        current++;
        List<String> acceptable = List.of(TYPE, NAMESPACE, NAME, ALIASES, FIELDS, DOC);
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
                case FIELDS:
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
        nodesBuilt.add((Node) builderStack.pop().build());
    }
}
