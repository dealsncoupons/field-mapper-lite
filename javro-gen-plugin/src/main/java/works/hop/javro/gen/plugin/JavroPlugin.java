package works.hop.javro.gen.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class JavroPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getExtensions().create("generator", JavroExtension.class);
        project.getTasks().register("generateJavro", GenerateTask.class);
    }
}
