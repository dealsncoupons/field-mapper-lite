package works.hop.field.model.builder;

import works.hop.field.model.Node;

import static works.hop.field.model.TokenType.ARRAY;

public class ArrayTypeBuilder extends AbstractTypeBuilder<Node> {

    @Override
    public Node build() {
        Node node = new Node(type, ARRAY);
        node.items = items;
        return node;
    }
}
