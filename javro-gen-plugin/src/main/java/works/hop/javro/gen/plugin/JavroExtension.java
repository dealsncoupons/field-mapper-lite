package works.hop.javro.gen.plugin;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import javax.inject.Inject;

public class JavroExtension {

    final Property<String> sourceDir;
    final Property<String> destDir;

    @Inject
    public JavroExtension(ObjectFactory objects) {
        this.sourceDir = objects.property(String.class);
        this.destDir = objects.property(String.class);;
    }
}
