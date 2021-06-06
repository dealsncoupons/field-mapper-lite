package works.hop.field.graph;

import java.util.LinkedList;
import java.util.List;

public class Vertex02<T> {

    private final T name;
    private final List<Vertex02<T>> neighbours;
    private boolean visited;
    private boolean beingVisited;

    public Vertex02(T name) {
        this.name = name;
        this.neighbours = new LinkedList<>();
    }

    public T getName() {
        return name;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isBeingVisited() {
        return beingVisited;
    }

    public void setBeingVisited(boolean beingVisited) {
        this.beingVisited = beingVisited;
    }

    public List<Vertex02<T>> getNeighbours() {
        return neighbours;
    }

    public void addNeighbour(Vertex02<T> edge) {
        this.neighbours.add(edge);
    }

    @Override
    public String toString() {
        return "[" + name + "]";
    }
}
