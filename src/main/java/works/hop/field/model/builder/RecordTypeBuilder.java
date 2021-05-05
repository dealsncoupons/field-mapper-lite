package works.hop.field.model.builder;

import works.hop.field.model.Node;

import java.util.ArrayList;
import java.util.List;

public class RecordTypeBuilder extends AbstractTypeBuilder<Node> {

    List<Node> fields = new ArrayList<>();

    @Override
    public AbstractTypeBuilder<Node> namespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    @Override
    public AbstractTypeBuilder<Node> add(Node node) {
        this.fields.add(node);
        return this;
    }

    @Override
    public Node build() {
        Node node = new Node(type, name, namespace);
        node.children.addAll(fields);
        return node;
    }
}
