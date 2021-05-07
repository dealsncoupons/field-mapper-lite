package works.hop.field.graph;

import java.util.LinkedList;
import java.util.List;

public class Vertex<T> {

    private final T name;
    private final List<Vertex<T>> neighbours = new LinkedList<>();
    private boolean visited;

    public Vertex(T name) {
        this.name = name;
    }

    public T getName() {
        return name;
    }

    public List<Vertex<T>> getNeighbours() {
        return neighbours;
    }

    public void addNeighbour(Vertex<T> vertex) {
        this.neighbours.add(vertex);
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    @Override
    public String toString() {
        return this.name.toString();
    }
}
