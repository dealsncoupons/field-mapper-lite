package works.hop.javro.gen.builder;

import works.hop.javro.gen.core.Node;

import static works.hop.javro.gen.core.TokenType.ARRAY;

public class ArrayTypeBuilder extends AbstractTypeBuilder<Node> {

    @Override
    public Node build() {
        Node node = new Node(type, ARRAY);
        node.items = items;
        return node;
    }
}
