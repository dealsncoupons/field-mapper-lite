package works.hop.field.model;

import java.util.LinkedList;
import java.util.List;

public class Node {

    String value;
    Node parent;
    List<Node> children = new LinkedList<>();

    public Node(String value, Node parent) {
        this.value = value;
        this.parent = parent;
    }

    public void child(Node child){
        this.children.add(child);
    }
}
