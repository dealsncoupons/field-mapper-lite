package works.hop.field.model.builder;

import works.hop.field.model.Node;

public class FieldTypeBuilder extends AbstractTypeBuilder<Node> {
    @Override
    public Node build() {
        return new Node(type, name, namespace);
    }
}
