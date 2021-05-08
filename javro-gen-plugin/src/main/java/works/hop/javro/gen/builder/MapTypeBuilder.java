package works.hop.javro.gen.builder;

import works.hop.javro.gen.core.Node;
import works.hop.javro.gen.core.TokenType;

public class MapTypeBuilder extends AbstractTypeBuilder<Node> {

    @Override
    public Node build() {
        Node node = new Node(type, TokenType.MAP);
        node.values = values;
        return node;
    }
}
