package works.hop.hydrate.gen.core;

import java.util.LinkedList;
import java.util.List;

public class Node {

    public String type;
    public String name;
    public String packageName;
    public String items;    //list item types
    public String values;   //map value types
    public List<String> symbols = new LinkedList<>(); //for enum types
    public List<Node> children = new LinkedList<>();
    public List<String> annotations = new LinkedList<>();

    public Node(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public void child(Node child) {
        this.children.add(child);
    }
}
