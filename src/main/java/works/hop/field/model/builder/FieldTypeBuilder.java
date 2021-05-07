package works.hop.field.model.builder;

import works.hop.field.model.Node;
import works.hop.field.model.TypeBuilder;

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
