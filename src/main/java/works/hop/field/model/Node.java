package works.hop.field.model;

import java.util.LinkedList;
import java.util.List;

public class Node {

    public String type;
    public String name;
    public String packageName;
    public List<Node> children = new LinkedList<>();

    public Node(String type, String name, String packageName) {
        this.type = type;
        this.name = name;
        this.packageName = packageName;
    }

    public void child(Node child) {
        this.children.add(child);
    }
}
