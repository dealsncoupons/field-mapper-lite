package works.hop.field.graph;

import java.util.LinkedList;
import java.util.List;

public class Vertex01<T> {

    private final T name;
    private int minDistance;
    private boolean visited;
    private Vertex01<T> predecessor;
    private final List<Edge<T>> neighbours;

    public Vertex01(T name) {
        this.name = name;
        this.minDistance = Integer.MAX_VALUE;
        this.neighbours  = new LinkedList<>();
    }

    public T getName() {
        return name;
    }

    public int getMinDistance() {
        return minDistance;
    }

    public void setMinDistance(int minDistance) {
        this.minDistance = minDistance;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public Vertex01<T> getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(Vertex01<T> predecessor) {
        this.predecessor = predecessor;
    }

    public List<Edge<T>> getNeighbours() {
        return neighbours;
    }

    public void addNeighbour(Edge<T> edge) {
        this.neighbours.add(edge);
    }

    @Override
    public String toString() {
        return "" + name + " - " + predecessor;
    }
}
