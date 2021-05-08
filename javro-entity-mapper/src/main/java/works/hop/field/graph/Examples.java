package works.hop.field.graph;

import java.util.HashMap;
import java.util.Map;

public class Examples {

    public static void main(String[] args) {
        System.out.println("traverse BFS");
        traverseBFSExample();
        System.out.println("traverse DFS with stack");
        traverseDFSWithStackExample();
        System.out.println("traverse DFS recursively");
        traverseDFSRecursivelyExample();
    }

    public static Map<String, Vertex<String>> createVertices() {
        Map<String, Vertex<String>> map = new HashMap<>();
        map.put("A", new Vertex<>("A"));
        map.put("B", new Vertex<>("B"));
        map.put("C", new Vertex<>("C"));
        map.put("D", new Vertex<>("D"));
        map.put("E", new Vertex<>("E"));
        map.put("F", new Vertex<>("F"));
        map.put("G", new Vertex<>("G"));
        map.put("H", new Vertex<>("H"));
        return map;
    }

    public static void createConnections(Map<String, Vertex<String>> map) {
        map.get("A").addNeighbour(map.get("B"));
        map.get("A").addNeighbour(map.get("F"));
        map.get("A").addNeighbour(map.get("G"));

        map.get("B").addNeighbour(map.get("A"));
        map.get("B").addNeighbour(map.get("C"));
        map.get("B").addNeighbour(map.get("D"));

        map.get("C").addNeighbour(map.get("B"));

        map.get("D").addNeighbour(map.get("B"));
        map.get("D").addNeighbour(map.get("E"));

        map.get("F").addNeighbour(map.get("A"));

        map.get("G").addNeighbour(map.get("A"));
        map.get("G").addNeighbour(map.get("H"));

        map.get("H").addNeighbour(map.get("G"));
    }

    public static void traverseBFSExample() {
        Map<String, Vertex<String>> map = createVertices();
        createConnections(map);
        BreadthFirstSearch bfs = new BreadthFirstSearch();
        bfs.traverse(map.get("A"));
    }

    public static void traverseDFSRecursivelyExample() {
        Map<String, Vertex<String>> map = createVertices();
        createConnections(map);
        DepthFirstSearch dfs = new DepthFirstSearch();
        dfs.traverseRecursively(map.get("A"));
    }

    public static void traverseDFSWithStackExample() {
        Map<String, Vertex<String>> map = createVertices();
        createConnections(map);
        DepthFirstSearch dfs = new DepthFirstSearch();
        dfs.traverseWithStack(map.get("A"));
    }
}
