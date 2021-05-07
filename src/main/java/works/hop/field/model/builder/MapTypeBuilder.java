package works.hop.field.model.builder;

import works.hop.field.model.Node;

import static works.hop.field.model.TokenType.MAP;

public class MapTypeBuilder extends AbstractTypeBuilder<Node> {

    @Override
    public Node build() {
        Node node = new Node(type, MAP);
        node.values = values;
        return node;
    }
}
