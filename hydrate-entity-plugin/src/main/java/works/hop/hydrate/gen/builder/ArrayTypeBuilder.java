package works.hop.hydrate.gen.builder;

import works.hop.hydrate.gen.core.Node;

import static works.hop.hydrate.gen.core.TokenType.ARRAY;

public class ArrayTypeBuilder extends AbstractTypeBuilder<Node> {

    @Override
    public Node build() {
        Node node = new Node(type, ARRAY);
        node.items = items;
        return node;
    }
}
