package works.hop.field.model;

import java.util.ArrayList;
import java.util.List;

import static works.hop.field.model.TokenType.START_OBJECT;

//  primitiveType() -> nullType() | booleanType() | intType() | longType() | floatType() | doubleType() | bytesType() | stringType()
//  complexType()   -> recordType() | enumType() | arrayType () | mapType() | fixedType()
//  fieldName()     -> string()
//  symbolsAttribute()  -> "symbols"
//  itemsAttribute()    -> "items"
//  symbolsProperty()   -> symbolsAttribute() keyValueSep() startArray() string()+ endArray()
//  recordDefinition()    -> startObject() typeProperty() attributeSep() nameProperty()
//                  -> [attributeSep() docProperty()] [attributeSep() aliasesProperty()]
//                  -> attributeSep() fieldsAttribute() attributeSep() startArray() fieldProperty()+ endArray()
//                  -> endObject()
//  enumDefinition()      -> startObject() typeProperty() attributeSep() nameProperty() symbolsProperty() endObject()
//  fieldProperty() -> startObject() nameProperty(), attributeSep(), typeProperty() endObject()
//  typeProperty()  -> typeAttribute() keyValueSep() (primitiveType() | complexType())
//  nameProperty()  -> nameAttribute() keyValueSep() fieldName()
//  sizeProperty()  -> sizeAttribute() keyValueSep() int()
//  itemsProperty() -> itemsAttribute() keyValueSep() ()string()
//  defaultListProperty()   -> defaultAttribute() keyValueSep() startArray() string()* endArray()
//  defaultMapProperty()   -> defaultAttribute() keyValueSep() startObject()  endObject()
//  aliasesProperty()   -> aliasesAttribute() keyValueSep() startArray() string()+ endArray()
//  enumDefinition()      -> startObject() typeProperty() attributeSep() nameProperty() symbolsProperty() endObject()
//  arrayDefinition()     -> startObject() typeProperty() attributeSep() itemsProperty() [attributeSep() defaultListProperty()] endObject()
//  mapDefinition()     -> startObject() typeProperty() attributeSep() valuesProperty() [attributeSep() defaultMapProperty()] endObject()
//  unionType()     -> startArray() string()+ endArray()
//  fixedDefinition()   -> startObject() typeProperty() attributeSep() sizeProperty() attributeSep() nameProperty() endObject()

public class Parser {

    final List<Token> tokens = new ArrayList<>();
    Node root;
    Node head;
    int current = 0;

    public Parser(List<Token> tokens) {
        if (tokens.isEmpty()) {
            throw new RuntimeException("Parser cannot function without tokens");
        }
        this.tokens.addAll(tokens);
    }

    public void recordType() {
//        Token token = tokens.get(current);
//        if(expectToken(START_OBJECT)){
//            current++;
//        }

    }

    public boolean record(String tokenType){
        return tokens.get(current).type == tokenType;
    }
}
