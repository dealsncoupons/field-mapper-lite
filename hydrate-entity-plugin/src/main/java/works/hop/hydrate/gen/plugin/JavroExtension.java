package works.hop.hydrate.gen.plugin;

import org.gradle.api.provider.Property;

import java.io.File;

public interface JavroExtension {

    Property<File> getSourceDir();

    Property<File> getDestDir();
}
