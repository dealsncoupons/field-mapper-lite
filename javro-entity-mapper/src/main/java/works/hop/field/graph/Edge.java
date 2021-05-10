package works.hop.field.graph;

public class Edge<T> {

    private final Vertex01<T> target;
    private final int weight;

    public Edge(Vertex01<T> target, int weight) {
        this.target = target;
        this.weight = weight;
    }

    public Vertex01<T> getTarget() {
        return target;
    }

    public int getWeight() {
        return weight;
    }
}
