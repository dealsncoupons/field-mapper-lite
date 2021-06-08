package works.hop.javro.gen.core;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class Progress {

    public static String DISPATCHERS = "dispatchers";

    final File destDir;
    final String structClass = "org.apache.kafka.connect.data.Struct";
    final String localCacheClass = "works.hop.hydrate.jdbc.context.LocalCache";
    final String changeConsumerClass = "works.hop.hydrate.jdbc.changes.ChangeConsumer";
    final String dispatcherInterface = "works.hop.hydrate.jdbc.changes.ChangeDispatcher";
    final List<String> dispatchers = new ArrayList<>();
    final TypesMap typesMap = TypesMap.instance(); //important to use this shared instance

    public Progress(File destDir) {
        this.destDir = destDir;
    }

    public void onEvent(String name, String packageName, String className) {
        if (name.equals(DISPATCHERS)) {
            this.dispatchers.add(String.format("%s.%s", packageName, className));
        }
    }

    private void generateChangesDispatcherChain() {
        String dispatcherChain = "ChangeDispatcherChain";
        if (!dispatchers.isEmpty()) {
            String packageName = dispatchers.get(0).substring(0, dispatchers.get(0).lastIndexOf("."));
            TypeSpec.Builder classBuilder = TypeSpec.classBuilder(dispatcherChain)
                    .addSuperinterface(typesMap.typeName(dispatcherInterface))
                    .addModifiers(Modifier.PUBLIC);

            //Add lock object to use when instantiating singleton
            classBuilder.addField(FieldSpec.builder(TypeName.OBJECT, "lock", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                    .initializer("new Object()")
                    .build());
            classBuilder.addField(FieldSpec.builder(typesMap.typeName(dispatcherChain), "instance", Modifier.PRIVATE, Modifier.STATIC)
                    .build());
            ClassName collectionClass = ClassName.get(Collection.class);
            TypeName collectionType = ParameterizedTypeName.get(collectionClass, typesMap.typeName(dispatcherInterface));
            classBuilder.addField(FieldSpec.builder(collectionType, "dispatchers", Modifier.PRIVATE, Modifier.FINAL).build());

            //Add private constructor
            classBuilder.addMethod(MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PRIVATE)
                    .addCode(CodeBlock.builder()
                            .addStatement("this.dispatchers = new $T<>()", ArrayList.class)
                            .addStatement("this.initialize()").build()).build());

            //Add static method to return instance
            classBuilder.addMethod(MethodSpec.methodBuilder("chain")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addCode(CodeBlock.builder()
                            .addStatement("synchronized (lock) {\n" +
                                    "    if (instance == null) {\n" +
                                    "        instance = new ChangeDispatcherChain();\n" +
                                    "    }\n" +
                                    "}\n" +
                                    "return instance").build())
                    .returns(ClassName.get(packageName, "ChangeDispatcherChain")).build());

            //implement 'canHandle' method
            MethodSpec canHandleMethod = MethodSpec.methodBuilder("canHandle")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ParameterSpec.builder(TypeName.get(String.class), "source").build())
                    .addCode(CodeBlock.builder()
                            .addStatement("return true").build())
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
                            .addStatement(" for (ChangeDispatcher changeDispatcher : dispatchers) {\n" +
                                            "    if (changeDispatcher.canHandle(source)) {\n" +
                                            "        changeDispatcher.dispatch(record, source, operation, cache);\n" +
                                            "        break;\n" +
                                            "    }\n" +
                                            "}",
                                    UUID.class).build())
                    .returns(TypeName.VOID)
                    .build();
            classBuilder.addMethod(dispatchMethod);

            //add 'initialize' method
            MethodSpec.Builder initMethodBuilder = MethodSpec.methodBuilder("initialize")
                    .addModifiers(Modifier.PUBLIC);
            CodeBlock.Builder initCode = CodeBlock.builder();
            for (String dispatcherClassName : dispatchers) {
                initCode.addStatement("this.dispatchers.add(new $L())", dispatcherClassName);
            }
            initCode.addStatement("$T.getInstance().register(this)", typesMap.typeName(changeConsumerClass));
            initMethodBuilder.addCode(initCode.build());
            classBuilder.addMethod(initMethodBuilder.build());

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
        }
    }

    public void complete() {
        generateChangesDispatcherChain();
    }
}
