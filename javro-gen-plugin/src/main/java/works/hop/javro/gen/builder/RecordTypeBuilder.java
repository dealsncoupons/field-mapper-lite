package works.hop.javro.gen.builder;

import works.hop.javro.gen.core.Node;

import java.util.ArrayList;
import java.util.List;

public class RecordTypeBuilder extends AbstractTypeBuilder<Node> {

    List<Node> fields = new ArrayList<>();
    List<String> annotations = new ArrayList<>();

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
    public AbstractTypeBuilder<Node> annotation(String annotation) {
        this.annotations.add(annotation);
        return this;
    }

    @Override
    public Node build() {
        Node node = new Node(type, name);
        node.packageName = namespace;
        node.children.addAll(fields);
        node.annotations.addAll(this.annotations);
        return node;
    }
}
