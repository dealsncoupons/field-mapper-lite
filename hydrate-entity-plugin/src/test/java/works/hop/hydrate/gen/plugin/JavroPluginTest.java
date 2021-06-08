package works.hop.hydrate.gen.plugin;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class JavroPluginTest {

    @Test
    public void greeterPluginAddsGenerateTaskToProject() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("works.hop.hydrate-entity-plugin");
        assertTrue(project.getTasks().getByName("generateJavro") instanceof GenerateTask);
    }
}