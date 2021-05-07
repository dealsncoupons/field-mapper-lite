package works.hop.javro.gen.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import works.hop.javro.gen.core.Parser;

public class GenerateTask extends DefaultTask {

    @Input
    Property<String> sourceDir;
    @Input
    Property<String> destDir;

    @TaskAction
    public void generatedSources(){
        String defaultSrcDir = "/src/main/resources/model";
        String defaultDestDir = "build/generated-sources/";
        String inputDir = sourceDir.getOrElse(defaultSrcDir);
        String outputDir = destDir.getOrElse(defaultDestDir);
        Parser.generateSources(inputDir, outputDir);
    }
}
