package works.hop.hydrate.gen.core;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TypesMap extends HashMap<String, TypeName> {

    private static final TypesMap instance = new TypesMap();

    private TypesMap() {
        put("null", null);
        put("boolean", ClassName.get(Boolean.class));
        put("int", ClassName.get(Integer.class));
        put("long", ClassName.get(Long.class));
        put("float", ClassName.get(Float.class));
        put("double", ClassName.get(Double.class));
        put("bytes", ClassName.get(Byte.class));
        put("string", ClassName.get(String.class));
    }

    public static TypesMap instance() {
        return instance;
    }

    public TypeName createFieldType(Node fieldNode) {
        switch (fieldNode.type) {
            case TokenType.ARRAY:
                ClassName list = ClassName.get(Collection.class);
                return ParameterizedTypeName.get(list, typeName(fieldNode.items));
            case TokenType.MAP:
                TypeName string = ClassName.get(String.class);
                ClassName map = ClassName.get(Map.class);
                return ParameterizedTypeName.get(map, string, typeName(fieldNode.values));
            default:
                return typeName(fieldNode.type);
        }
    }

    public TypeName typeName(String type) {
        if (this.containsKey(type)) {
            return this.get(type);
        } else {
            String[] splitType = splitQualifiedName(type);
            return ClassName.get(splitType[0], splitType[1]);
        }
    }

    public String[] splitQualifiedName(String type) {
        int lastDotIndex = type.lastIndexOf(".");
        boolean hasPackage = lastDotIndex > -1;
        String packageName = hasPackage ? type.substring(0, lastDotIndex) : "";
        String typeName = hasPackage ? type.substring(lastDotIndex + 1) : type;
        return new String[]{packageName, typeName};
    }
}
