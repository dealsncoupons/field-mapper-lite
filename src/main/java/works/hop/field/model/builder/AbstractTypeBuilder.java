package works.hop.field.model.builder;

import works.hop.field.model.Node;
import works.hop.field.model.TypeBuilder;

public abstract class AbstractTypeBuilder<T> implements TypeBuilder<T> {

    protected String type;
    protected String name;
    protected String namespace;
    protected String items;
    protected String values;

    @Override
    public String qualifiedName() {
        return namespace != null ?
                namespace + "." + name :
                name;
    }

    @Override
    public AbstractTypeBuilder<T> name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public AbstractTypeBuilder<T> value(String value) {
        TypeBuilder.logger().warn("property 'value' is not used in {}", getClass().getSimpleName());
        return this;
    }

    @Override
    public AbstractTypeBuilder<T> namespace(String namespace) {
        TypeBuilder.logger().warn("property 'namespace' is not used in {}", getClass().getSimpleName());
        return this;
    }

    @Override
    public AbstractTypeBuilder<T> type(String type) {
        this.type = type;
        return this;
    }

    @Override
    public AbstractTypeBuilder<T> add(Node node) {
        //override in base class if need be
        return this;
    }

    @Override
    public AbstractTypeBuilder<T> add(String symbol) {
        //override in base class if need be
        return this;
    }

    @Override
    public TypeBuilder<T> annotation(String annotations) {
        //override in base class if need be
        return this;
    }

    @Override
    public AbstractTypeBuilder<T> items(String items) {
        this.items = items;
        return this;
    }

    @Override
    public AbstractTypeBuilder<T> values(String values) {
        this.values = values;
        return this;
    }
}
