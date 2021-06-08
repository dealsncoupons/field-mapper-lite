package works.hop.hydrate.gen.builder;

import works.hop.hydrate.gen.core.Node;
import works.hop.hydrate.gen.core.TypeBuilder;

import java.util.ArrayList;
import java.util.List;

public class FieldTypeBuilder extends AbstractTypeBuilder<Node> {

    List<String> annotations = new ArrayList<>();

    @Override
    public TypeBuilder<Node> annotation(String annotation) {
        this.annotations.add(annotation);
        return this;
    }

    @Override
    public Node build() {
        Node node = new Node(type, name);
        node.values = values;
        node.items = items;
        node.annotations.addAll(this.annotations);
        return node;
    }
}
