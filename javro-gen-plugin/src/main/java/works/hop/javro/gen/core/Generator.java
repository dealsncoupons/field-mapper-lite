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
    final String baseInterface = "Unreflect";
    final TypesMap typesMap = TypesMap.instance(); //important to use this shared instance

    public Generator(Node node, List<String> additionalTypes, File destDir) {
        this.node = node;
        Map<String, TypeName> extraTypes = new HashMap<>();
        for (String type : additionalTypes) {
            String[] splitType = typesMap.splitQualifiedName(type);
            extraTypes.put(type, ClassName.get(splitType[0], splitType[1]));
        }
        this.typesMap.putAll(extraTypes);
        this.destDir = destDir;
    }

    public void generate() {
        if ("enum".equals(node.type)) {
            generateEnum();
        } else {
            generateBaseInterface();
            generateEntityPojo();
        }
    }

    public void generateBaseInterface() {
        //figure out the package name
        String packageName = node.packageName == null ? "" : node.packageName;

        TypeSpec.Builder interfaceBuilder = TypeSpec.interfaceBuilder(baseInterface)
                .addModifiers(Modifier.PUBLIC);

        TypeVariableName sourceReturnType = TypeVariableName.get("S", Object.class);
        MethodSpec sourceMethod = MethodSpec.methodBuilder("source")
                .addModifiers(Modifier.DEFAULT, Modifier.PUBLIC)
                .addTypeVariable(sourceReturnType)
                .returns(sourceReturnType)
                .addStatement("return null")
                .build();

        interfaceBuilder.addMethod(sourceMethod);

        TypeVariableName getReturnType = TypeVariableName.get("O", Object.class);
        MethodSpec getMethod = MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.DEFAULT, Modifier.PUBLIC)
                .addParameter(String.class, "property")
                .addTypeVariable(getReturnType)
                .returns(getReturnType)
                .addStatement("return null")
                .build();

        interfaceBuilder.addMethod(getMethod);

        TypeVariableName setParameterType = TypeVariableName.get("O", Object.class);
        MethodSpec setMethod = MethodSpec.methodBuilder("set")
                .addModifiers(Modifier.DEFAULT, Modifier.PUBLIC)
                .addParameter(String.class, "property")
                .addParameter(setParameterType, "value")
                .addTypeVariable(TypeVariableName.get("O", Object.class))
                .returns(TypeName.VOID)
                .build();

        interfaceBuilder.addMethod(setMethod);

        JavaFile interfaceFile = JavaFile.builder(packageName, interfaceBuilder.build())
                .addFileComment("This interface is AUTO-GENERATED, so there's no point of modifying it")
                .build();

        //write to file system
        try {
            interfaceFile.writeTo(destDir);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public void generateEntityPojo() {
        //figure out the package name
        String packageName = node.packageName == null ? "" : node.packageName;

        //class annotations
        List<AnnotationSpec> classAnnotations = new ArrayList<>();
        for (String annotationValue : node.annotations) {
            String annotationName = annotationValue.substring(0, annotationValue.indexOf("("));
            String[] splitType = typesMap.splitQualifiedName(annotationName);
            AnnotationSpec.Builder annotationBuilder = AnnotationSpec.builder(ClassName.get(splitType[0], splitType[1]));
            addMembers().accept(annotationValue, annotationBuilder);
            AnnotationSpec annotationSpec = annotationBuilder.build();
            classAnnotations.add(annotationSpec);
        }

        String entityInterface = String.format("I%s", node.name);
        TypeSpec.Builder interfaceBuilder = TypeSpec.interfaceBuilder(entityInterface)
                .addSuperinterface(ClassName.get(packageName, baseInterface))
                .addModifiers(Modifier.PUBLIC);

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(node.name)
                .addAnnotations(classAnnotations)
                .addSuperinterface(ClassName.get(packageName, entityInterface))
                .addModifiers(Modifier.PUBLIC);

        //create constructor
        MethodSpec defaultConstructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .build();
        classBuilder.addMethod(defaultConstructor);

        for (Node fieldNode : node.children) {
            //field annotations
            List<AnnotationSpec> fieldAnnotations = new ArrayList<>();
            for (String annotationValue : fieldNode.annotations) {
                String annotationName = truncate(annotationValue, annotationValue.indexOf("("));
                String[] splitType = typesMap.splitQualifiedName(annotationName);
                AnnotationSpec.Builder annotationBuilder = AnnotationSpec.builder(
                        ClassName.get(splitType[0], splitType[1]));
                addMembers().accept(annotationValue, annotationBuilder);
                AnnotationSpec annotationSpec = annotationBuilder
                        .build();
                fieldAnnotations.add(annotationSpec);
            }

            //add instance field
            FieldSpec fieldSpec = createFieldSpec(fieldNode, fieldAnnotations);
            classBuilder.addField(fieldSpec);

            //add setter
            String setterMethodName = "set" + capitalize(fieldNode.name);
            classBuilder.addMethod(generateInstanceSetMethod(typesMap.createFieldType(fieldNode), setterMethodName, fieldNode.name));
            interfaceBuilder.addMethod(generateAbstractSetMethod(typesMap.createFieldType(fieldNode), setterMethodName, fieldNode.name));

            //add getter
            String getterMethodName = "get" + capitalize(fieldNode.name);
            classBuilder.addMethod(generateInstanceGetMethod(typesMap.createFieldType(fieldNode), getterMethodName, fieldNode.name));
            interfaceBuilder.addMethod(generateAbstractGetMethod(typesMap.createFieldType(fieldNode), getterMethodName));
        }

        //implement methods in the baseInterface
        classBuilder.addMethod(generateBaseSourceMethod(packageName, entityInterface));
        classBuilder.addMethod(generateBaseGetMethod());
        classBuilder.addMethod(generateBaseSetMethod());

        //create both interface and concrete entity class files
        JavaFile entityInterfaceFile = JavaFile.builder(packageName, interfaceBuilder.build())
                .addFileComment("This entity interface is AUTO-GENERATED, so there's no point of modifying it")
                .build();

        JavaFile concreteEntityFile = JavaFile.builder(packageName, classBuilder.build())
                .addFileComment("This entity class is AUTO-GENERATED, so there's no point of modifying it")
                .build();

        //write to file system
        try {
            entityInterfaceFile.writeTo(destDir);
            concreteEntityFile.writeTo(destDir);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    private MethodSpec generateAbstractSetMethod(TypeName parameterType, String methodName, String paramName) {
        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                .addParameter(parameterType, paramName)
                .returns(TypeName.VOID)
                .build();
    }

    private MethodSpec generateInstanceSetMethod(TypeName parameterType, String methodName, String paramName) {
        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(parameterType, paramName)
                .addStatement("this.$L = $L", paramName, paramName)
                .returns(TypeName.VOID)
                .build();
    }

    private MethodSpec generateAbstractGetMethod(TypeName returnType, String methodName) {
        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                .returns(returnType)
                .build();
    }

    private MethodSpec generateInstanceGetMethod(TypeName returnType, String methodName, String paramName) {
        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(returnType)
                .addStatement("return this.$L", paramName)
                .build();
    }

    private MethodSpec generateBaseSourceMethod(String packageName, String interfaceName) {
        return MethodSpec.methodBuilder("source")
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(packageName, interfaceName))
                .addStatement("return this")
                .build();
    }

    private MethodSpec generateBaseSetMethod() {
        TypeVariableName returnType = TypeVariableName.get("O", Object.class);
        MethodSpec.Builder builder = MethodSpec.methodBuilder("set")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "property")
                .addParameter(returnType, "value")
                .addTypeVariable(TypeVariableName.get("O", Object.class))
                .returns(TypeName.VOID);

        CodeBlock.Builder code =
                CodeBlock.builder()
                        .beginControlFlow("switch (property)");
        for (Node fieldNode : node.children) {
            TypeName typeName = typesMap.createFieldType(fieldNode);
            String setter = "set" + capitalize(fieldNode.name);
            code.add("case $S: \n" +
                    "source().$L(($L) value); \n" +
                    "break;\n", fieldNode.name, setter, typeName.toString());
        }
        code.add("default: \n" +
                "break;\n");
        code.endControlFlow();

        builder.addCode(code.build());
        return builder.build();
    }

    private MethodSpec generateBaseGetMethod() {
        TypeVariableName returnType = TypeVariableName.get("O", Object.class);
        MethodSpec.Builder builder = MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "property")
                .addTypeVariable(returnType)
                .returns(returnType);

        CodeBlock.Builder code =
                CodeBlock.builder()
                        .beginControlFlow("switch (property)");

        for (Node fieldNode : node.children) {
            code.add("case $S: \n" +
                    "return (O) source().$L();\n", fieldNode.name, "get" + capitalize(fieldNode.name));
        }
        code.add("default: \n" +
                "return null;\n");
        code.endControlFlow();

        builder.addCode(code.build());
        return builder.build();
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

    private FieldSpec createFieldSpec(Node fieldNode, List<AnnotationSpec> fieldAnnotations) {
        switch (fieldNode.type) {
            case TokenType.ARRAY:
                ClassName list = ClassName.get(List.class);
                TypeName listType = ParameterizedTypeName.get(list, typesMap.typeName(fieldNode.items));
                return FieldSpec.builder(
                        listType,
                        fieldNode.name,
                        Modifier.PUBLIC)
                        .addAnnotations(fieldAnnotations)
                        .build();
            case TokenType.MAP:
                TypeName string = ClassName.get(String.class);
                ClassName map = ClassName.get(Map.class);
                TypeName mapType = ParameterizedTypeName.get(map, string, typesMap.typeName(fieldNode.values));
                return FieldSpec.builder(
                        mapType,
                        fieldNode.name,
                        Modifier.PUBLIC)
                        .addAnnotations(fieldAnnotations)
                        .build();
            default:
                return FieldSpec.builder(
                        typesMap.typeName(fieldNode.type),
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
}
