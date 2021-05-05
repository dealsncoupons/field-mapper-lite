package works.hop.field.model.builder;

import works.hop.field.model.Node;

import java.util.ArrayList;
import java.util.List;

public class EnumTypeBuilder extends AbstractTypeBuilder<Node> {

    private List<String> enumSymbols = new ArrayList<>();

    @Override
    public AbstractTypeBuilder<Node> add(String value) {
        this.enumSymbols.add(value);
        return this;
    }

    @Override
    public Node build() {
        return new Node(type, name, namespace);
    }
}
