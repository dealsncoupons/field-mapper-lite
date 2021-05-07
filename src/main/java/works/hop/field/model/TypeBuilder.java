package works.hop.field.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface TypeBuilder<T> {

    static Logger logger() {
        return LoggerFactory.getLogger(TypeBuilder.class);
    }

    String qualifiedName();

    TypeBuilder<T> name(String name);

    TypeBuilder<T> value(String value);

    TypeBuilder<T> namespace(String namespace);

    TypeBuilder<T> type(String type);

    TypeBuilder<T> add(Node value);

    TypeBuilder<T> add(String value);

    TypeBuilder<T> annotation(String annotations);

    TypeBuilder<T> items(String items);

    TypeBuilder<T> size(int size);

    TypeBuilder<T> defaultValue(String defaultValue);

    T build();
}
