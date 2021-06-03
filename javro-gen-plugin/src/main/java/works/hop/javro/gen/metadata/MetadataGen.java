package works.hop.javro.gen.metadata;

import com.squareup.javapoet.*;
import works.hop.javro.gen.core.Node;
import works.hop.javro.gen.core.TypesMap;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MetadataGen implements OnReadyListener {

    public static final String metadataClassName = "EntityMetadata";
    public static final String packageNameProperty = "entity.metadata.package";

    private final List<Node> readyList;
    private final File destDir;
    private final String packageName;
    private final TypesMap typesMap = TypesMap.instance(); //important to use this shared instance

    public MetadataGen(File destDir) {
        this.readyList = new LinkedList<>();
        this.destDir = destDir;
        this.packageName = System.getProperty(packageNameProperty, "");
    }

    public void generate() {
        TypeSpec.Builder metadataClassBuilder = TypeSpec.classBuilder(metadataClassName)
                .addModifiers(Modifier.PUBLIC);
        metadataClassBuilder.addField(generateEntityInfoField());
        metadataClassBuilder.addMethod(generateGetEntityInfoMethod(readyList));

        JavaFile metadataFile = JavaFile.builder(packageName, metadataClassBuilder.build())
                .addFileComment("This metadata class is AUTO-GENERATED, so there's no point of modifying it")
                .build();

        //write to file system
        try {
            metadataFile.writeTo(destDir);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    private FieldSpec generateEntityInfoField() {
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(Function.class, Class.class, EntityInfo.class);
        return FieldSpec.builder(parameterizedTypeName, "entityInfoByType")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .initializer("getEntityInfoByType()")
                .build();
    }

    private MethodSpec generateGetEntityInfoMethod(List<Node> nodes) {
        CodeBlock.Builder codeBlock = CodeBlock.builder()
                .beginControlFlow("return entityType -> ");

        for (Node node : nodes) {
            codeBlock.beginControlFlow("if ($L.class.isAssignableFrom(entityType))", node.name);
            codeBlock.addStatement("return get%LInfo().get()", node.name);
            codeBlock.endControlFlow();
        }
        codeBlock.addStatement("throw new RuntimeException(\"Unknown entity type - \" + entityType.getName()");
        codeBlock.endControlFlow();

        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(Function.class, Class.class, EntityInfo.class);
        return MethodSpec.methodBuilder("getEntityInfoByType")
                .addModifiers(Modifier.STATIC, Modifier.PRIVATE)
                .addCode(codeBlock.build())
                .returns(parameterizedTypeName)
                .build();
    }

    private EntityInfo nodeToEntityInfo(Node node) {
        EntityInfo entityInfo = new EntityInfo();
        if (node.annotations.isEmpty()) {
            entityInfo.setTableName(node.name);
        } else {
            List<String> attributes = annotationAttributes(node.annotations.get(0));
            entityInfo.setTableName(getAttributeValue("value", node.name, attributes));
        }
        for (Node childNode : node.children) {
            FieldInfoBuilder builder = FieldInfoBuilder.builder();
            builder.name(childNode.name);
            builder.type(typesMap.createFieldType(childNode).getClass());
            builder.columnName(childNode.name);
            //discover additional details from annotations
            for (String annotation : childNode.annotations) {
                int lastDotIndex = annotation.lastIndexOf(".");
                int firstBracketIndex = annotation.indexOf("(");
                boolean hasPackage = lastDotIndex > -1;
                boolean hasAttributes = firstBracketIndex > -1;
                String annotationName = hasPackage && hasAttributes ? annotation.substring(lastDotIndex + 1, firstBracketIndex) :
                        hasPackage ? annotation.substring(lastDotIndex + 1) : annotation;
                List<String> attributes = annotationAttributes(annotation);
                addAnnotationInfo(childNode, annotationName, attributes, builder);
            }
            entityInfo.getFields().add(builder.build());
        }
        return entityInfo;
    }

    private List<String> annotationAttributes(String fullAnnotation) {
        String annotation = fullAnnotation;
        int lastDotIndex = fullAnnotation.lastIndexOf(".");
        if (lastDotIndex > -1) {
            annotation = fullAnnotation.substring(lastDotIndex + 1);
        }
        int firstBracketIndex = annotation.indexOf("(");
        int startIndex = firstBracketIndex > -1 ? firstBracketIndex + 1 : 0;
        int lastBracketIndex = annotation.lastIndexOf(")");
        int endIndex = lastBracketIndex > -1 ? lastBracketIndex - 1 : annotation.length();

        String annotationName = firstBracketIndex > -1 ? annotation.substring(0, firstBracketIndex) : annotation;

        List<String> attributes = Stream.of(annotation.substring(startIndex, endIndex).trim()
                .replaceFirst("\\W*([_\\w]+)\\W*", "$1")
                .split(",")).flatMap(pair -> Arrays.stream(pair.split("=")))
                .collect(Collectors.toList());
        attributes.add(0, annotationName);
        return attributes;
    }

    private void addAnnotationInfo(Node node, String annotationName, List<String> attributes, FieldInfoBuilder builder) {
        if (!attributes.isEmpty()) {
            switch (annotationName) {
                case "Column":
                    builder.columnName(getAttributeValue("value", node.name, attributes));
                    break;
                case "JoinColumn":
                    builder.columnName(getAttributeValue("value", node.name, attributes));
                    builder.joinTable(getAttributeValue("fkTable", node.name, attributes));
                    break;
                case "Id":
                    builder.isId(true);
                    break;
                default:
                    break;
            }
        }
    }

    private String getAttributeValue(String key, String defValue, List<String> list) {
        if (list.isEmpty()) {
            return defValue;
        } else {
            if (key.equals("name")) {
                return list.get(0);
            } else {
                int index = list.indexOf(key);
                if (index > -1) {
                    return list.get(index + 1);
                } else if (key.equals("value")) {
                    return list.get(1);
                } else {
                    return defValue;
                }
            }
        }
    }

    @Override
    public void completed(List<Node> readyList) {
        this.readyList.addAll(readyList);
    }

//    private CodeBlock generateEntityInfoSupplierCode(EntityI node) {
//        CodeBlock.Builder codeBlock = CodeBlock.builder()
//                .beginControlFlow("private static Supplier<EntityInfo> accountInfo ()");
//
//        for(Node child : node.children){
//            codeBlock.addStatement("FieldInfo %L = new FieldInfo(true, %L.class, %S, %S)", child.name, child.type, child.name, child.);
//        }
//        StringBuilder builder = new StringBuilder();
//        builder.append("private static Supplier<EntityInfo> accountInfo () {\n");
//        for(Node child : node.children) {
//            builder.append("FieldInfo id = new FieldInfo(true, UUID.class, \"id\", \"id\");\n");
//        }
//        builder.append("return () -> new EntityInfo(\"tbl_account\", List.of(id, dateCreated, lastUpdated, username, accessCode, member));\n");
//        builder.append("}");
//        return builder.toString();
//    }
}
