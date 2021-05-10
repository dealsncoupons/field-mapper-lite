package works.hop.field.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TopologicalOrdering<T> {

    private final Stack<Vertex<T>> sorted;

    public TopologicalOrdering() {
        this.sorted = new Stack<>();
    }

    public Stack<Vertex<T>> getSorted() {
        return sorted;
    }

    public void dfs(Vertex<T> vertex){
        vertex.setVisited(true);
        for(Vertex<T> neighbour : vertex.getNeighbours()){
            if(!neighbour.isVisited()){
                dfs(neighbour);
            }
        }

        sorted.push(vertex);
    }

    public static void main(String[] args) {
        TopologicalOrdering<String> topSort = new TopologicalOrdering<>();

        //prepare graph nodes
        List<Vertex<String>> graph = new ArrayList<>();
        graph.add(new Vertex<>("0"));
        graph.add(new Vertex<>("1"));
        graph.add(new Vertex<>("2"));
        graph.add(new Vertex<>("3"));
        graph.add(new Vertex<>("4"));
        graph.add(new Vertex<>("5"));

        graph.get(2).addNeighbour(graph.get(3));

        graph.get(3).addNeighbour(graph.get(1));

        graph.get(4).addNeighbour(graph.get(0));
        graph.get(4).addNeighbour(graph.get(1));

        graph.get(5).addNeighbour(graph.get(0));
        graph.get(5).addNeighbour(graph.get(2));

        //sort the graph topologically using dfs
        for(int i = 0; i < graph.size(); i++){
            if(!graph.get(i).isVisited()){
                topSort.dfs(graph.get(i));
            }
        }

        //print sorted graph by popping stack
        Stack<Vertex<String>> ordering = topSort.getSorted();
        for(int i = 0; i < graph.size(); i++){
            System.out.println(ordering.pop() + " -> ");
        }
    }
}
