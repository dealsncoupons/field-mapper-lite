package works.hop.javro.gen.core;

import com.squareup.javapoet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Generator {

    final Node node;
    final File destDir;
    final String hydrateInterface = "works.hop.upside.context.Hydrate";
    final String entityInfoClass = "works.hop.upside.relations.EntityInfo";
    final String fieldInfoClass = "works.hop.upside.relations.FieldInfo";
    final String fieldInfoBuilderClass = "works.hop.upside.relations.FieldInfoBuilder";
    final String dbSelectClass = "works.hop.upside.context.DbSelect";
    final String localCacheClass = "works.hop.upside.context.LocalCache";
    final String insertTemplateClass = "works.hop.upside.context.InsertTemplate";
    final String entityQueryClass = "works.hop.upside.relations.EntityQuery";
    final String structClass = "org.apache.kafka.connect.data.Struct";
    final String dispatcherInterface = "works.hop.upside.entity.dispatcher.ChangeDispatcher";
    final String[] metadataAnnotation = {"works.hop.javro.jdbc.annotation", "Metadata"};
    final TypesMap typesMap = TypesMap.instance(); //important to use this shared instance
    private Progress progress;

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
            generateEntityInterface();
            generateEntityPojo();
            generateEntityEventDispatcher();
        }
    }

    private void generateEntityEventDispatcher() {
        Optional<String> tableNameAnnotation = node.annotations.stream().filter(item -> item.contains(".Table")).findFirst();
        String packageName = node.packageName == null ? "dispatcher" : String.format("%s.dispatcher", node.packageName);
        if (tableNameAnnotation.isPresent()) {
            //figure out the package name
            String dispatcherClassName = String.format("%sEventDispatcher", node.name);
            String annotation = tableNameAnnotation.get();
            String tableName = annotation.substring(annotation.indexOf("(") + 1, annotation.lastIndexOf(")")).replaceAll("\\\\\"", "");

            TypeSpec.Builder classBuilder = TypeSpec.classBuilder(dispatcherClassName)
                    .addSuperinterface(typesMap.typeName(dispatcherInterface))
                    .addModifiers(Modifier.PUBLIC);

            //implement 'canHandle' method
            MethodSpec canHandleMethod = MethodSpec.methodBuilder("canHandle")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ParameterSpec.builder(TypeName.get(String.class), "source").build())
                    .addCode(CodeBlock.builder()
                            .addStatement("String table = $S", tableName)
                            .addStatement("return table.equals(source)").build())
                    .returns(TypeName.BOOLEAN)
                    .build();
            classBuilder.addMethod(canHandleMethod);

            //implement 'dispatch' method
            MethodSpec dispatchMethod = MethodSpec.methodBuilder("dispatch")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ParameterSpec.builder(typesMap.typeName(structClass), "record").build())
                    .addParameter(ParameterSpec.builder(TypeName.get(String.class), "source").build())
                    .addParameter(ParameterSpec.builder(TypeName.get(String.class), "operation").build())
                    .addParameter(ParameterSpec.builder(typesMap.typeName(localCacheClass), "cache").build())
                    .addCode(CodeBlock.builder()
                            .beginControlFlow(" if(record != null)")
                            .addStatement("cache.get($T.fromString(record.getString(\"id\")), source).ifPresent(entity -> entity.refresh(record))",
                                    UUID.class)
                            .endControlFlow().build())
                    .returns(TypeName.VOID)
                    .build();
            classBuilder.addMethod(dispatchMethod);

            //create entity interface file
            JavaFile dispatcherFile = JavaFile.builder(packageName, classBuilder.build())
                    .addFileComment("This change event handler is AUTO-GENERATED, so there's no point of modifying it")
                    .build();

            //write to file system
            try {
                dispatcherFile.writeTo(destDir);
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }

            //add dispatcher name to collection of dispatchers
            progress.onEvent(Progress.DISPATCHERS, packageName, dispatcherClassName);
        }
    }

    public void generateEntityInterface() {
        //figure out the package name
        String packageName = node.packageName == null ? "" : node.packageName;
        String interfaceName = String.format("I%s", node.name);

        TypeSpec.Builder interfaceBuilder = TypeSpec.interfaceBuilder(interfaceName)
                .addSuperinterface(typesMap.typeName(hydrateInterface))
                .addModifiers(Modifier.PUBLIC);

        for (Node fieldNode : node.children) {
            //add getter methods
            String getterMethodName = "get" + capitalize(fieldNode.name);
            interfaceBuilder.addMethod(generateAbstractGetMethod(typesMap.createFieldType(fieldNode), getterMethodName));
        }

        //add initEntityInfo method
        MethodSpec initEntityInfo = MethodSpec.methodBuilder("initEntityInfo")
                .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                .returns(typesMap.typeName(entityInfoClass))
                .build();
        interfaceBuilder.addMethod(initEntityInfo);

        //create entity interface file
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
            int endIndex = annotationValue.indexOf("(");
            String annotationName = endIndex > -1 ? annotationValue.substring(0, endIndex) : annotationValue;
            String[] splitType = typesMap.splitQualifiedName(annotationName);
            AnnotationSpec.Builder annotationBuilder = AnnotationSpec.builder(ClassName.get(splitType[0], splitType[1]));
            addMembers().accept(annotationValue, annotationBuilder);
            AnnotationSpec annotationSpec = annotationBuilder.build();
            classAnnotations.add(annotationSpec);
        }

        String entityInterface = String.format("I%s", node.name);
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(node.name)
                .addAnnotations(classAnnotations)
                .addSuperinterface(ClassName.get(packageName, entityInterface))
                .addModifiers(Modifier.PUBLIC);

        //create default constructor
        MethodSpec defaultConstructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addCode(CodeBlock.builder()
                        .addStatement("this.entityInfo = initEntityInfo()").build())
                .build();
        classBuilder.addMethod(defaultConstructor);

        //create all-args constructor
        MethodSpec.Builder allArgsConstructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);
        for (Node fieldNode : node.children) {
            allArgsConstructor.addParameter(generateArgParameter(typesMap.createFieldType(fieldNode), fieldNode.name));
            allArgsConstructor.addCode(CodeBlock.builder()
                    .addStatement("this.$N = $N", fieldNode.name, fieldNode.name)
                    .build());
        }
        allArgsConstructor.addCode(CodeBlock.builder()
                .addStatement("this.entityInfo = initEntityInfo()").build());
        classBuilder.addMethod(allArgsConstructor.build());

        //add logger field
        FieldSpec loggerField = FieldSpec.builder(ClassName.get(Logger.class), "log")
                .addModifiers(Modifier.STATIC, Modifier.FINAL, Modifier.PRIVATE)
                .initializer("$T.getLogger($T.class)", LoggerFactory.class, typesMap.typeName(node.name))
                .build();
        classBuilder.addField(loggerField);

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

            //implement getter methods
            String getterMethodName = "get" + capitalize(fieldNode.name);
            classBuilder.addMethod(generateInstanceGetMethod(typesMap.createFieldType(fieldNode), getterMethodName, fieldNode.name));
        }

        //add entityInfo instance variable
        classBuilder.addField(FieldSpec.builder(typesMap.typeName(entityInfoClass), "entityInfo")
                .addAnnotation(ClassName.get(metadataAnnotation[0], metadataAnnotation[1]))
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build());

        //implement methods in the parent interfaces
        classBuilder.addMethod(generateEntityInfoMethod());
        classBuilder.addMethod(generateInitEntityInfoMethod());
        classBuilder.addMethod(generateBaseGetMethod());
        classBuilder.addMethod(generateBaseSetMethod());
        classBuilder.addMethod(generateRefreshMethod());

        if (node.annotations.stream().anyMatch(a -> a.contains(".Table"))) {
            classBuilder.addMethod(generateInsertMethod());
            classBuilder.addMethod(generateSelectMethod());
            classBuilder.addMethod(generateUpdateMethod());
            classBuilder.addMethod(generateDeleteMethod());
        }

        if (node.annotations.stream().anyMatch(a -> a.contains(".Embeddable"))) {
            classBuilder.addMethod(generateEmbeddableInsertMethod());
            classBuilder.addMethod(generateEmbeddableSelectMethod());
            classBuilder.addMethod(generateEmbeddableUpdateMethod());
            classBuilder.addMethod(generateEmbeddableDeleteMethod());
        }

        //create entity concrete class file
        JavaFile concreteEntityFile = JavaFile.builder(packageName, classBuilder.build())
                .addFileComment("This entity class is AUTO-GENERATED, so there's no point of modifying it")
                .build();

        //write to file system
        try {
            concreteEntityFile.writeTo(destDir);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    private MethodSpec generateEmbeddableSelectMethod() {
        TypeVariableName returnType = TypeVariableName.get("E", typesMap.typeName(hydrateInterface));
        MethodSpec.Builder builder = MethodSpec.methodBuilder("select")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(ClassName.get(ResultSet.class), "rs").build())
                .addParameter(ParameterSpec.builder(typesMap.typeName(dbSelectClass), "resolver").build())
                .addParameter(ParameterSpec.builder(ClassName.get(Connection.class), "connection").build())
                .addParameter(ParameterSpec.builder(typesMap.typeName(localCacheClass), "cache").build())
                .addTypeVariable(returnType)
                .returns(returnType);

        CodeBlock.Builder codeBuilder = CodeBlock.builder()
                .add("try {\n" +
                        "    for($T field : getEntityInfo().getFields()){\n" +
                        "        set(field.name, rs.getObject(field.columnName, field.type));\n" +
                        "    }\n" +
                        "} catch ($T e) {\n" +
                        "    e.printStackTrace();\n" +
                        "    throw new RuntimeException(\"Cannot resolve a property\", e);\n" +
                        "}\n" +
                        "return (E)this;", typesMap.typeName(fieldInfoClass), SQLException.class);

        builder.addCode(codeBuilder.build());
        return builder.build();
    }

    private MethodSpec generateEmbeddableInsertMethod() {
        TypeVariableName returnType = TypeVariableName.get("E", typesMap.typeName(hydrateInterface));
        MethodSpec.Builder builder = MethodSpec.methodBuilder("insert")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(ClassName.get(Connection.class), "connection").build())
                .addTypeVariable(returnType)
                .returns(returnType);

        CodeBlock.Builder codeBuilder = CodeBlock.builder()
                .add(CodeBlock.builder()
                        .addStatement("log.warn($S)", "For embeddable entities, the insert method is not usable")
                        .addStatement("return (E) this")
                        .build());

        builder.addCode(codeBuilder.build());
        return builder.build();
    }

    private MethodSpec generateEmbeddableUpdateMethod() {
        TypeVariableName returnType = TypeVariableName.get("E", typesMap.typeName(hydrateInterface));
        ParameterizedTypeName mapParameter = ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(String.class), ParameterizedTypeName.get(Object.class));
        MethodSpec.Builder builder = MethodSpec.methodBuilder("update")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(mapParameter, "columnValues").build())
                .addParameter(ParameterSpec.builder(ClassName.get(Connection.class), "connection").build())
                .addTypeVariable(returnType)
                .returns(returnType);

        CodeBlock.Builder codeBuilder = CodeBlock.builder()
                .add(CodeBlock.builder()
                        .addStatement("log.warn($S)", "For embeddable entities, the update method is not usable")
                        .addStatement("return (E) this")
                        .build());

        builder.addCode(codeBuilder.build());
        return builder.build();
    }

    private MethodSpec generateEmbeddableDeleteMethod() {
        TypeVariableName returnType = TypeVariableName.get("E", typesMap.typeName(hydrateInterface));
        MethodSpec.Builder builder = MethodSpec.methodBuilder("delete")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(ClassName.get(Connection.class), "connection").build())
                .addTypeVariable(returnType)
                .returns(returnType);

        CodeBlock.Builder codeBuilder = CodeBlock.builder()
                .add(CodeBlock.builder()
                        .addStatement("log.warn($S)", "For embeddable entities, the delete method is not usable")
                        .addStatement("return (E) this")
                        .build());

        builder.addCode(codeBuilder.build());
        return builder.build();
    }

    private ParameterSpec generateArgParameter(TypeName parameterType, String name) {
        return ParameterSpec.builder(parameterType, name)
                .addModifiers(Modifier.FINAL)
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
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(returnType)
                .addStatement("return this.$L", paramName)
                .build();
    }

    private MethodSpec generateEntityInfoMethod() {
        return MethodSpec.methodBuilder("getEntityInfo")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addCode(CodeBlock.builder()
                        .addStatement("return this.entityInfo").build())
                .returns(typesMap.typeName(entityInfoClass))
                .build();
    }

    private MethodSpec generateInitEntityInfoMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("initEntityInfo")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(typesMap.typeName(entityInfoClass));

        CodeBlock.Builder codeBuilder = CodeBlock.builder()
                .addStatement("$T<$T> fields = new $T<>()", List.class, typesMap.typeName(fieldInfoClass), ArrayList.class)
                .addStatement("EntityInfo entityInfo = new EntityInfo()")
                .addStatement("entityInfo.setTableName($S)", inferTableName(node));

        for (Node child : node.children) {
            StringBuilder fieldBuilder = new StringBuilder("$T $N = $T.builder().name($S)");
            child.annotations.forEach(annotation -> {
                if (annotation.contains(".Id")) {
                    fieldBuilder.append(".isId(true)");
                }
                if (annotation.contains(".Embedded")) {
                    fieldBuilder.append(".embedded(true)");
                }
                if (annotation.contains(".Temporal")) {
                    fieldBuilder.append(".temporal(true)");
                }
                if (annotation.contains(".Column")) {
                    int startIndex = annotation.indexOf("(");
                    int endIndex = annotation.indexOf(")");
                    Map<String, String> attributes = Arrays.stream(annotation.substring(startIndex + 1, endIndex)
                            .split(","))
                            .map(input -> {
                                String[] splits = input.replaceAll("\\\\\"", "").split("=");
                                return splits.length == 1 ? new String[]{"value", splits[0]} : splits;
                            })
                            .collect(Collectors.toMap(pair -> pair[0].trim(), pair -> pair[1].trim()));
                    fieldBuilder.append(".columnName(\"").append(attributes.get("value")).append("\")");
                    if (attributes.containsKey("updatable")) {
                        fieldBuilder.append(".updatable(").append(attributes.get("updatable")).append(")");
                    }
                }
                if (annotation.contains(".JoinColumn")) {
                    int startIndex = annotation.indexOf("(");
                    int endIndex = annotation.indexOf(")");
                    Map<String, String> attributes = Arrays.stream(annotation.substring(startIndex + 1, endIndex)
                            .split(","))
                            .map(input -> {
                                String[] splits = input.replaceAll("\\\\\"", "").split("=");
                                return splits.length == 1 ? new String[]{"value", splits[0]} : splits;
                            })
                            .collect(Collectors.toMap(pair -> pair[0].trim(), pair -> pair[1].trim()));
                    fieldBuilder.append(".relational(true)")
                            .append(".columnName(\"").append(attributes.get("value")).append("\")");
                    if (attributes.containsKey("fkTable")) {
                        fieldBuilder.append(".joinTable(\"").append(attributes.get("fkTable")).append("\")");
                    }
                    if (attributes.containsKey("updatable")) {
                        fieldBuilder.append(".updatable(").append(attributes.get("updatable")).append(")");
                    }
                    if (attributes.containsKey("manyToOne")) {
                        fieldBuilder
                                .append(".type(").append(child.items).append(".class").append(")")
                                .append(".collection(").append(attributes.get("manyToOne")).append(")");
                    } else {
                        fieldBuilder.append(".type(").append(child.type).append(".class").append(")");
                    }
                }

                if (!child.type.equals("string") && fieldBuilder.indexOf(".type(") == -1) {
                    fieldBuilder.append(".type(").append(child.type).append(".class").append(")");
                }
            });
            codeBuilder.addStatement(fieldBuilder.append(".build()").toString(), typesMap.typeName(fieldInfoClass),
                    child.name, typesMap.typeName(fieldInfoBuilderClass), child.name);
            codeBuilder.addStatement("fields.add($N)", child.name);
        }

        builder.addCode(codeBuilder
                .addStatement("entityInfo.setFields(fields)")
                .addStatement("return entityInfo").build());
        return builder.build();
    }

    private MethodSpec generateInsertMethod() {
        TypeVariableName returnType = TypeVariableName.get("E", typesMap.typeName(hydrateInterface));
        MethodSpec.Builder builder = MethodSpec.methodBuilder("insert")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(ClassName.get(Connection.class), "connection").build())
                .addTypeVariable(returnType)
                .returns(returnType);

        CodeBlock.Builder codeBuilder = CodeBlock.builder()
                .add("getEntityInfo().getFields().stream().filter(fieldInfo -> fieldInfo.isRelational).forEach(fieldInfo -> {\n" +
                                "    if (!fieldInfo.isCollection) {\n" +
                                "        if (this.get(fieldInfo.name) != null) {\n" +
                                "            set(fieldInfo.name, $T.insertOne(get(fieldInfo.name), connection));\n" +
                                "        }\n" +
                                "    } else {\n" +
                                "        if (this.get(fieldInfo.name) != null) {\n" +
                                "            $T<Hydrate> collection = get(fieldInfo.name);\n" +
                                "            set(fieldInfo.name, collection.stream().map(entity -> InsertTemplate.insertOne(entity, connection)).collect($T.toList()));\n" +
                                "        }\n" +
                                "    }\n" +
                                "});\n" +
                                "\n" +
                                "Map<String, $T<Object>> parameters = new $T<>();\n" +
                                "extractEntityValues(parameters, this, this.entityInfo);\n" +
                                "\n" +
                                "String[] orderedColumns = parameters.entrySet().stream().filter(entry -> entry.getValue().isPresent())\n" +
                                "        .map(Map.Entry::getKey).toArray(String[]::new);\n" +
                                "String query = $T.getInstance().insertOne(entityInfo.getTableName(), orderedColumns);\n" +
                                "\n" +
                                "try ($T ps = connection.prepareStatement(query, $T.RETURN_GENERATED_KEYS)) {\n" +
                                "    for (int i = 0; i < orderedColumns.length; i++) { //maintains order of columns-to-values as they appear in the query\n" +
                                "        if(parameters.get(orderedColumns[i]).isPresent()) {\n" +
                                "            ps.setObject(i + 1, parameters.get(orderedColumns[i]).get());\n" +
                                "        }\n" +
                                "    }\n" +
                                "\n" +
                                "    int rowsAffected = ps.executeUpdate();\n" +
                                "    log.info(\"{} row(s) affected after insert operation\", rowsAffected);\n" +
                                "\n" +
                                "    try ($T keys = ps.getGeneratedKeys()) {\n" +
                                "       if (keys.next()) {\n" +
                                "          this.id = UUID.fromString(keys.getString(1));\n" +
                                "       }\n" +
                                "    } catch ($T e) {\n" +
                                "       e.printStackTrace();\n" +
                                "       log.warn(\"Could not retrieve generated id value\", e);\n" +
                                "    }\n" +
                                "\n" +
                                "} catch (SQLException e) {\n" +
                                "    e.printStackTrace();\n" +
                                "    throw new $T(\"Problem executing insert query\", e);\n" +
                                "}\n" +
                                "\n" +
                                "return (E) this;\n", typesMap.typeName(insertTemplateClass), Collection.class, Collectors.class,
                        Optional.class, LinkedHashMap.class, typesMap.typeName(entityQueryClass), PreparedStatement.class,
                        Statement.class, ResultSet.class, SQLException.class, RuntimeException.class);
        builder.addCode(codeBuilder.build());
        return builder.build();
    }

    private MethodSpec generateSelectMethod() {
        TypeVariableName returnType = TypeVariableName.get("E", typesMap.typeName(hydrateInterface));
        MethodSpec.Builder builder = MethodSpec.methodBuilder("select")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(ClassName.get(ResultSet.class), "rs").build())
                .addParameter(ParameterSpec.builder(typesMap.typeName(dbSelectClass), "resolver").build())
                .addParameter(ParameterSpec.builder(ClassName.get(Connection.class), "connection").build())
                .addParameter(ParameterSpec.builder(typesMap.typeName(localCacheClass), "cache").build())
                .addTypeVariable(returnType)
                .returns(returnType);

        CodeBlock.Builder codeBuilder = CodeBlock.builder()
                .add("try {\n" +
                        "    this.id = rs.getObject(\"id\", UUID.class);\n" +
                        "    String tableName =getEntityInfo().getTableName();\n" +
                        "    if (cache.get(this.id, tableName).isPresent()) {\n" +
                        "        return (E) cache.get(this.id, tableName).get();\n" +
                        "    } else {\n" +
                        "        cache.add(this.id, this, tableName);\n" +
                        "        for($T field : getEntityInfo().getFields()){\n" +
                        "            if(field.isRelational){\n" +
                        "                if(!field.isCollection){\n" +
                        "                    UUID fieldId = rs.getObject(field.columnName, UUID.class);\n" +
                        "                    if (fieldId != null) {\n" +
                        "                        set(field.name, resolver.selectByIdColumn((Hydrate)field.type.getConstructor().newInstance(), field.joinTable, \"id\", fieldId, connection));\n" +
                        "                    }\n" +
                        "                }\n" +
                        "                else{\n" +
                        "                    set(field.name, resolver.selectByJoinColumn(Task::new, entityInfo.getTableName(), \"id\", field.joinTable, field.columnName, field.columnName, this.id, connection));\n" +
                        "                }\n" +
                        "            }\n" +
                        "            else if(field.isEmbedded){\n" +
                        "                Hydrate embeddedField = get(field.name);\n" +
                        "                if(embeddedField != null){\n" +
                        "                    embeddedField.select(rs, resolver, connection, cache);\n" +
                        "                }\n" +
                        "                else{\n" +
                        "                    set(field.name, ((Hydrate)field.type.getConstructor().newInstance()).select(rs, resolver, connection, cache));\n" +
                        "                }\n" +
                        "            }\n" +
                        "            else{\n" +
                        "                if(field.isTemporal) {\n" +
                        "                     set(field.name, rs.getObject(field.columnName, field.type));\n" +
                        "                 }\n" +
                        "                 else{\n" +
                        "                     set(field.name, rs.getObject(field.columnName));\n" +
                        "                 }\n" +
                        "            }\n" +
                        "        }\n" +
                        "    }\n" +
                        "} catch ($T | $T | $T | $T | $T e) {\n" +
                        "    e.printStackTrace();\n" +
                        "    throw new RuntimeException(\"Cannot resolve a property\", e);\n" +
                        "}\n" +
                        "return (E)this;", typesMap.typeName(fieldInfoClass), SQLException.class, NoSuchMethodException.class, InstantiationException.class, IllegalAccessException.class, InvocationTargetException.class);

        builder.addCode(codeBuilder.build());
        return builder.build();
    }

    private MethodSpec generateUpdateMethod() {
        TypeVariableName returnType = TypeVariableName.get("E", typesMap.typeName(hydrateInterface));
        ParameterizedTypeName mapParameter = ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(String.class), ParameterizedTypeName.get(Object.class));
        MethodSpec.Builder builder = MethodSpec.methodBuilder("update")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(mapParameter, "columnValues").build())
                .addParameter(ParameterSpec.builder(ClassName.get(Connection.class), "connection").build())
                .addTypeVariable(returnType)
                .returns(returnType);

        CodeBlock.Builder codeBuilder = CodeBlock.builder()
                .add("columnValues.forEach(this::set);\n" +
                                "\n" +
                                "Map<String, $T<Object>> parameters = new $T<>();\n" +
                                "extractEntityValues(parameters, this, this.entityInfo);\n" +
                                "\n" +
                                "String[] idColumns = {\"id\"};\n" +
                                "String[] valueColumns = parameters.keySet().stream()\n" +
                                "        .filter(o -> $T.stream(idColumns).noneMatch(i -> i.equals(o))).toArray(String[]::new);\n" +
                                "String query = $T.getInstance().updateOne(entityInfo.getTableName(), idColumns, valueColumns);\n" +
                                "\n" +
                                "String[] orderedColumns = Arrays.copyOf(valueColumns, valueColumns.length + idColumns.length);\n" +
                                "System.arraycopy(idColumns, 0, orderedColumns, valueColumns.length, idColumns.length);\n" +
                                "try ($T ps = connection.prepareStatement(query)) {\n" +
                                "    for (int i = 0; i < orderedColumns.length; i++) { //maintains order of columns-to-values as they appear in the query\n" +
                                "        ps.setObject(i + 1, parameters.get(orderedColumns[i]).get());\n" +
                                "    }\n" +
                                "\n" +
                                "    int rowsAffected = ps.executeUpdate();\n" +
                                "    log.info(\"{} row(s) affected after update operation\", rowsAffected);\n" +
                                "} catch ($T e) {\n" +
                                "    e.printStackTrace();\n" +
                                "    throw new RuntimeException(\"Problem executing update query\", e);\n" +
                                "}\n" +
                                "\n" +
                                "return (E) this;", Optional.class, LinkedHashMap.class, Arrays.class, typesMap.typeName(entityQueryClass),
                        PreparedStatement.class, SQLException.class);

        builder.addCode(codeBuilder.build());
        return builder.build();
    }

    private MethodSpec generateDeleteMethod() {
        TypeVariableName returnType = TypeVariableName.get("E", typesMap.typeName(hydrateInterface));
        MethodSpec.Builder builder = MethodSpec.methodBuilder("delete")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(ClassName.get(Connection.class), "connection").build())
                .addTypeVariable(returnType)
                .returns(returnType);

        CodeBlock.Builder codeBuilder = CodeBlock.builder()
                .add("String query = $T.getInstance().deleteOne(entityInfo.getTableName(), new String[]{\"id\"});\n" +
                        " try ($T ps = connection.prepareStatement(query)) {\n" +
                        "     ps.setObject(1, this.getId());\n" +
                        "\n" +
                        "     int rowsAffected = ps.executeUpdate();\n" +
                        "     log.info(\"{} row(s) affected after delete operation\", rowsAffected);\n" +
                        " } catch ($T e) {\n" +
                        "     e.printStackTrace();\n" +
                        "     throw new RuntimeException(\"Problem executing delete query\", e);\n" +
                        " }\n" +
                        " return (E)this;", typesMap.typeName(entityQueryClass), PreparedStatement.class, SQLException.class);

        builder.addCode(codeBuilder.build());
        return builder.build();
    }

    private MethodSpec generateRefreshMethod() {
        TypeVariableName returnType = TypeVariableName.get("E", typesMap.typeName(hydrateInterface));
        MethodSpec.Builder builder = MethodSpec.methodBuilder("refresh")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(typesMap.typeName(structClass), "record").build())
                .addTypeVariable(returnType)
                .returns(returnType);

        CodeBlock.Builder codeBuilder = CodeBlock.builder()
                .add(" entityInfo.getFields().forEach(field -> {\n" +
                        "     if(!field.isCollection){\n" +
                        "         set(field.name, field.type.cast(record.get(field.columnName)));\n" +
                        "     }\n" +
                        " });\n" +
                        " return (E) this;");

        builder.addCode(codeBuilder.build());
        return builder.build();
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
            code.add("case $S: \n" +
                    "this.$L = ($L) value; \n" +
                    "break;\n", fieldNode.name, fieldNode.name, typeName.toString());
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
                    "return (O) this.$L;\n", fieldNode.name, fieldNode.name);
        }
        code.add("default: \n" +
                "return null;\n");
        code.endControlFlow();

        builder.addCode(code.build());
        return builder.build();
    }

    public void generateEnum() {
        TypeSpec.Builder builder = TypeSpec.enumBuilder(node.name).addModifiers(Modifier.PUBLIC);

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
                ClassName list = ClassName.get(Collection.class);
                TypeName listType = ParameterizedTypeName.get(list, typesMap.typeName(fieldNode.items));
                return FieldSpec.builder(
                        listType,
                        fieldNode.name,
                        Modifier.PRIVATE)
                        .addAnnotations(fieldAnnotations)
                        .build();
            case TokenType.MAP:
                TypeName string = ClassName.get(String.class);
                ClassName map = ClassName.get(Map.class);
                TypeName mapType = ParameterizedTypeName.get(map, string, typesMap.typeName(fieldNode.values));
                return FieldSpec.builder(
                        mapType,
                        fieldNode.name,
                        Modifier.PRIVATE)
                        .addAnnotations(fieldAnnotations)
                        .build();
            default:
                return FieldSpec.builder(
                        typesMap.typeName(fieldNode.type),
                        fieldNode.name,
                        Modifier.PRIVATE)
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
                                String value = keyValue[1].trim();
                                String format = value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false") ?
                                        "$L" : "$S";
                                annotationBuilder.addMember(keyValue[0].trim(), format, value);
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

    private String inferTableName(Node node) {
        return node.annotations.stream()
                .filter(annotation -> annotation.contains(".Table"))
                .map(annotation -> {
                    Pattern membersPattern = Pattern.compile("\\(\\\\\"(.*)\\\\\"\\)");
                    Matcher matcher = membersPattern.matcher(annotation);
                    if (matcher.find()) {
                        return matcher.group(1);
                    } else {
                        return node.name;
                    }
                }).findFirst().orElse("");
    }

    public void listener(Progress progress) {
        this.progress = progress;
    }
}
