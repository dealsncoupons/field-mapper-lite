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
        Node node = new Node(type, name);
        node.packageName = namespace;
        node.symbols.addAll(enumSymbols);
        return node;
    }
}
