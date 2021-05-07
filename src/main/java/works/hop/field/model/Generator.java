package works.hop.field.model;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Generator {

    final Node node;
    final String destDir = "build/generated/sources/";
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

    public Generator(Node node) {
        this.node = node;
    }

    public TypeName typeName(String type) {
        if (typeMap.containsKey(type)) {
            return typeMap.get(type);
        } else {
            try {
                return TypeName.get(Class.forName(type));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("You need to define generate the type '" + type + "' first before using it", e);
            }
        }
    }

    public void generate() {
        if ("enum".equals(node.type)) {
            generateEnum();
        } else {
            generateClass();
        }
    }

    public void generateClass() {
        //class annotations
        List<AnnotationSpec> classAnnotations = new ArrayList<>();
        for (String annotationValue : node.annotations) {
            String annotationName = annotationValue.substring(0, annotationValue.indexOf("("));
            try {
                AnnotationSpec.Builder annotationBuilder = AnnotationSpec.builder(Class.forName(annotationName));
                addMembers().accept(annotationValue, annotationBuilder);
                AnnotationSpec annotationSpec = annotationBuilder
                        .build();
                classAnnotations.add(annotationSpec);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException("Could not find the '" + annotationName + "' annotation");
            }
        }
        TypeSpec.Builder builder = TypeSpec.classBuilder(node.name)
                .addAnnotations(classAnnotations)
                .addModifiers(Modifier.PUBLIC);

        //create constructor
        MethodSpec defaultConstructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .build();
        builder.addMethod(defaultConstructor);

        for (Node child : node.children) {
            //field annotations
            List<AnnotationSpec> fieldAnnotations = new ArrayList<>();
            for (String annotationValue : child.annotations) {
                String annotationName = annotationValue.substring(0, annotationValue.indexOf("("));
                try {
                    AnnotationSpec.Builder annotationBuilder = AnnotationSpec.builder(Class.forName(annotationName));
                    addMembers().accept(annotationValue, annotationBuilder);
                    AnnotationSpec annotationSpec = annotationBuilder
                            .build();
                    fieldAnnotations.add(annotationSpec);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Could not find the '" + annotationName + "' annotation");
                }
            }

            //add instance field
            FieldSpec fieldSpec = FieldSpec.builder(
                    typeName(child.type),
                    child.name,
                    Modifier.PROTECTED)
                    .addAnnotations(fieldAnnotations)
                    .build();
            builder.addField(fieldSpec);

            //add setter
            MethodSpec setter = MethodSpec.methodBuilder("set" + capitalize(child.name))
                    .returns(TypeName.VOID)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(typeName(child.type), child.name)
                    .addStatement("this.$L = $L", child.name, child.name)
                    .build();
            builder.addMethod(setter);

            //add getter
            MethodSpec getter = MethodSpec.methodBuilder("set" + capitalize(child.name))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(typeName(child.type))
                    .addStatement("return this.$L", child.name)
                    .build();
            builder.addMethod(getter);
        }

        //add package name
        String packageName = node.packageName == null ? "" : node.packageName;
        JavaFile javaFile = JavaFile.builder(packageName, builder.build())
                .addFileComment("AUTO-GENERATED by JavaPoet")
                .build();

        //write to file system
        try {
            javaFile.writeTo(Paths.get(destDir));
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
            javaFile.writeTo(Paths.get(destDir));
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public BiConsumer<String, AnnotationSpec.Builder> addMembers() {
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
                                annotationBuilder.addMember(keyValue[0], "$S", keyValue[1]);
                            } else {
                                annotationBuilder.addMember("value", "$S", pair);
                            }
                        }
                    }
                }
            }
        };
    }

    public String capitalize(String input) {
        return String.format("%s%s", Character.toUpperCase(input.charAt(0)),
                input.substring(1));
    }
}
