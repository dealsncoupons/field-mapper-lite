package works.hop.javro.gen.plugin;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import spock.lang.Specification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("look into this further later on")
public class JavroPluginIntegrationTest extends Specification {

    Path testProjectDir = Files.createTempDirectory("test");
    String srcDir;
    String destDir;

    public JavroPluginIntegrationTest() throws IOException {
    }

    @Before
    public void setUp() throws IOException {
        Path srcDirPath = testProjectDir.getFileName().resolve("source");
        Path destDirPath = testProjectDir.getFileName().resolve("generated");

        Files.createDirectories(testProjectDir);
        Files.createDirectories(srcDirPath);
        Files.createDirectories(destDirPath);

        File sample = Paths.get(System.getProperty("user.dir"), "src/test/resources/model/ex1.avsc").toFile();
        try (FileChannel sourceChannel = new FileInputStream(sample).getChannel();
             FileChannel outputChannel = new FileOutputStream(srcDirPath.toFile()).getChannel()) {
            outputChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }

        srcDir = srcDirPath.getFileName().toString();
        destDir = destDirPath.getFileName().toString();
    }

    @After
    public void tearDown() {
        testProjectDir.toFile().deleteOnExit();
    }

    @Test
    public void generateSampleFile() throws IOException {
        String taskConfiguration = "" +
                "generateSources {" +
                "   srcDir: " + srcDir +
                "   destDir: " + destDir +
                "}";

        //write task configuration into build file in project root
        Path buildFile = Files.createFile(testProjectDir.getFileName().resolve("build.gradle"));
        try (FileChannel outputChannel = new FileOutputStream(buildFile.toFile()).getChannel()) {
            outputChannel.write(ByteBuffer.wrap(taskConfiguration.getBytes(StandardCharsets.UTF_8)));
        }

        //run gradle
        BuildResult generateSources = GradleRunner.create()
                .withProjectDir(testProjectDir.toFile())
                .withArguments("generateSources")
                .withPluginClasspath()
                .build();

        System.out.println(generateSources);
        assertThat(Files.exists(
                testProjectDir.getRoot().resolve("destDir/FullNameHolder.java"))).isTrue();
    }
}
