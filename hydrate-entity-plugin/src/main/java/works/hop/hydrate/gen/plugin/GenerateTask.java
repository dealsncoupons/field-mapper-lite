package works.hop.hydrate.gen.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import works.hop.hydrate.gen.core.Parser;

import java.io.File;
import java.nio.file.Paths;

public abstract class GenerateTask extends DefaultTask {

    @InputDirectory
    public abstract Property<File> getSourceDir();

    @OutputDirectory
    public abstract Property<File> getDestDir();

    @TaskAction
    public void generateJavro() {
        File defaultSrcDir = Paths.get(getProject().getProjectDir().getPath()).resolve("src/main/resources/avro").toFile();
        File defaultDestDir = Paths.get(getProject().getProjectDir().getPath()).resolve("build/generated-sources/").toFile();
        File inputDir = getSourceDir().getOrElse(defaultSrcDir);
        File outputDir = getDestDir().getOrElse(defaultDestDir);
        System.out.printf("INPUT_DIR - %s, OUTPUT_DIR - %s%n", inputDir.getPath(), outputDir.getPath());
        Parser.generateJavroUsingDir(inputDir, outputDir);
    }
}
