package works.hop.hydrate.gen.builder;

import works.hop.hydrate.gen.core.Node;
import works.hop.hydrate.gen.core.TokenType;

public class MapTypeBuilder extends AbstractTypeBuilder<Node> {

    @Override
    public Node build() {
        Node node = new Node(type, TokenType.MAP);
        node.values = values;
        return node;
    }
}
