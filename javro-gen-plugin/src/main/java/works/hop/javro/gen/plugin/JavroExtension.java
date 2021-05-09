package works.hop.javro.gen.plugin;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import javax.inject.Inject;
import java.io.File;

public class JavroExtension {

    Property<File> sourceDir;
    Property<File> destDir;

    @Inject
    public JavroExtension(ObjectFactory objects) {
        this.sourceDir = objects.property(File.class);
        this.destDir = objects.property(File.class);
        ;
    }
}
