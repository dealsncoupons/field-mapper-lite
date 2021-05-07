package works.hop.field.graph;

import java.util.LinkedList;
import java.util.Queue;

public class BreadthFirstSearch {

    public void traverse(Vertex<String> root) {
        Queue<Vertex<String>> queue = new LinkedList<>();
        queue.add(root);
        //mark as visited
        root.setVisited(true);

        while (!queue.isEmpty()) {
            Vertex<String> vertex = queue.poll();
            System.out.println("Visiting node " + vertex);

            for (Vertex<String> neighbour : vertex.getNeighbours()) {
                if (!neighbour.isVisited()) {
                    queue.add(neighbour);
                    neighbour.setVisited(true);
                }
            }
        }
    }
}
