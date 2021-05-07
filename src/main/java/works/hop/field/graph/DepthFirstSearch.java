package works.hop.field.graph;

import java.util.Stack;

public class DepthFirstSearch {

    public void traverseWithStack(Vertex<String> root) {
        Stack<Vertex<String>> stack = new Stack<>();
        stack.add(root);

        while (!stack.isEmpty()) {
            Vertex<String> vertex = stack.pop();
            if(!vertex.isVisited()) {
                vertex.setVisited(true);
                System.out.println("Visiting node " + vertex);
                stack.addAll(vertex.getNeighbours());
            }
        }
    }

    public void traverseRecursively(Vertex<String> root) {
        System.out.println("Visiting node " + root);
        if (!root.getNeighbours().isEmpty()) {
            root.setVisited(true);
            for (Vertex<String> vertex : root.getNeighbours()) {
                if (!vertex.isVisited()) {
                    traverseRecursively(vertex);
                }
            }
        }
    }
}
