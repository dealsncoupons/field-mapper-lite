package works.hop.field.model.builder;

import works.hop.field.model.Node;
import works.hop.field.model.TypeBuilder;

import java.util.ArrayList;
import java.util.List;

public class ArrayTypeBuilder extends AbstractTypeBuilder<Node> {

    List<TypeBuilder<?>> arrayValues = new ArrayList<>();

    @Override
    public Node build() {
        return new Node(type, name, namespace);
    }
}
