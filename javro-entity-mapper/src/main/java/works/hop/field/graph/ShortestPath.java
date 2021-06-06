package works.hop.field.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ShortestPath {

    final TopologicalOrdering01<String> topSorted;

    public ShortestPath(List<Vertex01<String>> graph) {
        this.topSorted = new TopologicalOrdering01<>(graph);
        graph.get(0).setMinDistance(0);
    }

    public static void main(String[] args) {
        Vertex01<String> v0 = new Vertex01<>("S");
        Vertex01<String> v1 = new Vertex01<>("A");
        Vertex01<String> v2 = new Vertex01<>("B");
        Vertex01<String> v3 = new Vertex01<>("C");
        Vertex01<String> v4 = new Vertex01<>("D");
        Vertex01<String> v5 = new Vertex01<>("E");

        v0.addNeighbour(new Edge<>(v1, 1));
        v0.addNeighbour(new Edge<>(v3, 2));

        v1.addNeighbour(new Edge<>(v2, 6));

        v2.addNeighbour(new Edge<>(v4, 1));
        v2.addNeighbour(new Edge<>(v5, 2));

        v3.addNeighbour(new Edge<>(v1, 4));
        v3.addNeighbour(new Edge<>(v4, 3));

        v4.addNeighbour(new Edge<>(v5, 1));

        //add vertices to list
        List<Vertex01<String>> graph = new ArrayList<>();
        graph.add(v0);
        graph.add(v1);
        graph.add(v2);
        graph.add(v3);
        graph.add(v4);
        graph.add(v5);

        //find shorted path
        ShortestPath shortestPath = new ShortestPath(graph);
        shortestPath.compute();

        //print to see results
        for (Vertex01<String> v : graph) {
            System.out.println("distance from s: " + v.getMinDistance() + " - " + v.getPredecessor());
        }
    }

    public void compute() {
        Stack<Vertex01<String>> sortedStack = topSorted.getSorted();
        while (!sortedStack.isEmpty()) {
            Vertex01<String> u = sortedStack.pop();
            for (Edge<String> e : u.getNeighbours()) {
                Vertex01<String> v = e.getTarget();

                if (v.getMinDistance() > u.getMinDistance() + e.getWeight()) {
                    v.setMinDistance((u.getMinDistance() + e.getWeight()));
                    v.setPredecessor(u);
                }
            }
        }
    }
}
