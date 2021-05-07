package works.hop.javro.gen.builder;

import works.hop.dto.javro.core.Node;
import works.hop.javro.gen.core.Node;
import works.hop.javro.gen.core.TokenType;

import static works.hop.dto.javro.core.TokenType.MAP;

public class MapTypeBuilder extends AbstractTypeBuilder<Node> {

    @Override
    public Node build() {
        Node node = new Node(type, TokenType.MAP);
        node.values = values;
        return node;
    }
}
