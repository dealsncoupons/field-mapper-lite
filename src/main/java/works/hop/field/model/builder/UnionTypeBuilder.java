package works.hop.field.model.builder;

import works.hop.field.model.Node;

import java.util.ArrayList;
import java.util.List;

public class UnionTypeBuilder extends AbstractTypeBuilder<Node> {

    private List<String> unionTypes = new ArrayList<>();

    @Override
    public AbstractTypeBuilder<Node> add(String value) {
        this.unionTypes.add(value);
        return this;
    }

    @Override
    public Node build() {
        String firstNonNullType = unionTypes.stream().filter(item -> !"null".equals(item)).findFirst().get();
        return new Node(firstNonNullType, name);
    }
}
