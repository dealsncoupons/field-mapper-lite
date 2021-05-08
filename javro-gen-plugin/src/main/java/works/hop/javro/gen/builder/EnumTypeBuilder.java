package works.hop.javro.gen.builder;


import works.hop.javro.gen.core.Node;

import java.util.ArrayList;
import java.util.List;

public class EnumTypeBuilder extends AbstractTypeBuilder<Node> {

    private final List<String> enumSymbols = new ArrayList<>();

    @Override
    public AbstractTypeBuilder<Node> add(String symbol) {
        this.enumSymbols.add(symbol);
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
