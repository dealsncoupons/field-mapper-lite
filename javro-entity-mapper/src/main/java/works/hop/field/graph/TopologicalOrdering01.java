package works.hop.field.graph;

import java.util.List;
import java.util.Stack;

public class TopologicalOrdering01<T> {

    private final Stack<Vertex01<T>> sorted;

    public TopologicalOrdering01(List<Vertex01<T>> graph) {
        this.sorted = new Stack<>();

        for (Vertex01<T> vertex : graph) {
            if (!vertex.isVisited()) {
                dfs(vertex);
            }
        }
    }

    public Stack<Vertex01<T>> getSorted() {
        return sorted;
    }

    public void dfs(Vertex01<T> vertex) {
        vertex.setVisited(true);

        for (Edge<T> neighbour : vertex.getNeighbours()) {
            if (!neighbour.getTarget().isVisited()) {
                dfs(neighbour.getTarget());
            }
        }

        sorted.push(vertex);
    }
}
