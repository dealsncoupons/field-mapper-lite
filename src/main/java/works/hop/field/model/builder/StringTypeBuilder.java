package works.hop.field.model.builder;

public class StringTypeBuilder extends AbstractTypeBuilder<String> {

    String value;

    @Override
    public AbstractTypeBuilder<String> value(String value) {
        this.value = value;
        return this;
    }

    @Override
    public String build() {
        return this.value;
    }
}
