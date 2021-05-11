package works.hop.javro.gen.core;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Generator {

    final Node node;
    final File destDir;
    final Map<String, TypeName> typeMap = new HashMap<>() {
        {
            put("null", null);
            put("boolean", TypeName.BOOLEAN);
            put("int", TypeName.INT);
            put("long", TypeName.LONG);
            put("float", TypeName.FLOAT);
            put("double", TypeName.DOUBLE);
            put("bytes", TypeName.BYTE);
            put("string", TypeName.get(String.class));
        }
    };

    public Generator(Node node, List<String> additionalTypes, File destDir) {
        this.node = node;
        Map<String, TypeName> extraTypes = new HashMap<>();
        for (String type : additionalTypes) {
            String[] splitType = splitQualifiedName(type);
            extraTypes.put(type, ClassName.get(splitType[0], splitType[1]));
        }
        this.typeMap.putAll(extraTypes);
        this.destDir = destDir;
    }

    public void generate() {
        if ("enum".equals(node.type)) {
            generateEnum();
        } else {
            generateEntityInterface();
            generateEntityPojo();
        }
    }


    public void generateEntityInterface(){

    }

    public void generateEntityPojo() {
        //class annotations
        List<AnnotationSpec> classAnnotations = new ArrayList<>();
        for (String annotationValue : node.annotations) {
            String annotationName = annotationValue.substring(0, annotationValue.indexOf("("));
            String[] splitType = splitQualifiedName(annotationName);
            AnnotationSpec.Builder annotationBuilder = AnnotationSpec.builder(
                    ClassName.get(splitType[0], splitType[1]));
            addMembers().accept(annotationValue, annotationBuilder);
            AnnotationSpec annotationSpec = annotationBuilder
                    .build();
            classAnnotations.add(annotationSpec);
        }
        TypeSpec.Builder builder = TypeSpec.classBuilder(node.name)
                .addAnnotations(classAnnotations)
                .addModifiers(Modifier.PUBLIC);

        //create constructor
        MethodSpec defaultConstructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .build();
        builder.addMethod(defaultConstructor);

        for (Node fieldNode : node.children) {
            //field annotations
            List<AnnotationSpec> fieldAnnotations = new ArrayList<>();
            for (String annotationValue : fieldNode.annotations) {
                String annotationName = truncate(annotationValue, annotationValue.indexOf("("));
                String[] splitType = splitQualifiedName(annotationName);
                AnnotationSpec.Builder annotationBuilder = AnnotationSpec.builder(
                        ClassName.get(splitType[0], splitType[1]));
                addMembers().accept(annotationValue, annotationBuilder);
                AnnotationSpec annotationSpec = annotationBuilder
                        .build();
                fieldAnnotations.add(annotationSpec);
            }

            //add instance field
            FieldSpec fieldSpec = createFieldSpec(fieldNode, fieldAnnotations);
            builder.addField(fieldSpec);

            //add setter
            MethodSpec setter = MethodSpec.methodBuilder("set" + capitalize(fieldNode.name))
                    .returns(TypeName.VOID)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(createFieldType(fieldNode), fieldNode.name)
                    .addStatement("this.$L = $L", fieldNode.name, fieldNode.name)
                    .build();
            builder.addMethod(setter);

            //add getter
            MethodSpec getter = MethodSpec.methodBuilder("get" + capitalize(fieldNode.name))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(createFieldType(fieldNode))
                    .addStatement("return this.$L", fieldNode.name)
                    .build();
            builder.addMethod(getter);
        }

        //add package name
        String packageName = node.packageName == null ? "" : node.packageName;
        JavaFile javaFile = JavaFile.builder(packageName, builder.build())
                .addFileComment("This class is AUTO-GENERATED, so there's no point of modifying it")
                .build();

        //write to file system
        try {
            javaFile.writeTo(destDir);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public void generateEnum() {
        TypeSpec.Builder builder = TypeSpec.enumBuilder(node.name)
                .addModifiers(Modifier.PUBLIC);

        //add symbols
        for (String symbol : node.symbols) {
            builder.addEnumConstant(symbol);
        }

        //add package name
        String packageName = node.packageName == null ? "" : node.packageName;
        JavaFile javaFile = JavaFile.builder(packageName, builder.build())
                .addFileComment("AUTO-GENERATED file")
                .build();

        //write to file system
        try {
            javaFile.writeTo(destDir);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    private TypeName createFieldType(Node fieldNode) {
        switch (fieldNode.type) {
            case TokenType.ARRAY:
                ClassName list = ClassName.get(List.class);
                return ParameterizedTypeName.get(list, typeName(fieldNode.items));
            case TokenType.MAP:
                TypeName string = ClassName.get(String.class);
                ClassName map = ClassName.get(Map.class);
                return ParameterizedTypeName.get(map, string, typeName(fieldNode.values));
            default:
                return typeName(fieldNode.type);
        }
    }

    private FieldSpec createFieldSpec(Node fieldNode, List<AnnotationSpec> fieldAnnotations) {
        switch (fieldNode.type) {
            case TokenType.ARRAY:
                ClassName list = ClassName.get(List.class);
                TypeName listType = ParameterizedTypeName.get(list, typeName(fieldNode.items));
                return FieldSpec.builder(
                        listType,
                        fieldNode.name,
                        Modifier.PUBLIC)
                        .addAnnotations(fieldAnnotations)
                        .build();
            case TokenType.MAP:
                TypeName string = ClassName.get(String.class);
                ClassName map = ClassName.get(Map.class);
                TypeName mapType = ParameterizedTypeName.get(map, string, typeName(fieldNode.values));
                return FieldSpec.builder(
                        mapType,
                        fieldNode.name,
                        Modifier.PUBLIC)
                        .addAnnotations(fieldAnnotations)
                        .build();
            default:
                return FieldSpec.builder(
                        typeName(fieldNode.type),
                        fieldNode.name,
                        Modifier.PUBLIC)
                        .addAnnotations(fieldAnnotations)
                        .build();
        }
    }

    private BiConsumer<String, AnnotationSpec.Builder> addMembers() {
        return (String annotationValue, AnnotationSpec.Builder annotationBuilder) -> {
            if (annotationValue.contains("(")) {
                Pattern membersPattern = Pattern.compile("\\((.*)\\)");
                Matcher matcher = membersPattern.matcher(annotationValue);
                if (matcher.find()) {
                    String matched = matcher.group(1);
                    if (matched.trim().length() > 0) {
                        String[] pairs = matched.trim().replace("\\\"", "").split(",");
                        for (String pair : pairs) {
                            if (pair.contains("=")) {
                                String[] keyValue = pair.split("=");
                                annotationBuilder.addMember(keyValue[0].trim(), "$S", keyValue[1].trim());
                            } else {
                                annotationBuilder.addMember("value", "$S", pair.trim());
                            }
                        }
                    }
                }
            }
        };
    }

    private String capitalize(String input) {
        return String.format("%s%s", Character.toUpperCase(input.charAt(0)),
                input.substring(1));
    }

    private String truncate(String input, int end) {
        return end < 0 ? input : input.substring(0, end);
    }

    private String[] splitQualifiedName(String type) {
        int lastDotIndex = type.lastIndexOf(".");
        boolean hasPackage = lastDotIndex > -1;
        String packageName = hasPackage ? type.substring(0, lastDotIndex) : "";
        String typeName = hasPackage ? type.substring(lastDotIndex + 1) : type;
        return new String[]{packageName, typeName};
    }

    private TypeName typeName(String type) {
        if (typeMap.containsKey(type)) {
            return typeMap.get(type);
        } else {
            String[] splitType = splitQualifiedName(type);
            return ClassName.get(splitType[0], splitType[1]);
        }
    }
}
