package works.hop.field.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.stream.IntStream;

public class MazeMap {

    final int rows;
    final int cols;
    final int size;
    final Map<Integer, Vertex<Integer>> vertices = new HashMap<>();

    public MazeMap(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.size = rows * cols;
        IntStream.range(0, size).forEach(cell -> vertices.put(cell, new Vertex<>(cell)));
    }

    public void addConnection(int cellA, int cellB){
        vertices.get(cellA).addNeighbour(vertices.get(cellB));
    }

    public void traverse(int startCell, int endCell){
        Stack<Vertex<Integer>> stack = new Stack<>();
        stack.add(vertices.get(startCell));

        end:
        while(!stack.isEmpty()){
            Vertex<Integer> vertex = stack.pop();
            vertex.setVisited(true);
            for(Vertex<Integer> neighbour : vertex.getNeighbours()){
                if(!neighbour.isVisited()){
                    System.out.println("Visiting " + neighbour.getName());
                    stack.add(neighbour);
                    if(neighbour.getName() == endCell){
                        System.out.println("Found exit point");
                        break end;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        MazeMap maze =new MazeMap(5, 5);
        //add connections
        maze.addConnection(6, 7);
        maze.addConnection(7, 6);
        maze.addConnection(7, 12);
        maze.addConnection(12, 7);
        maze.addConnection(12, 17);
        maze.addConnection(17, 12);
        maze.addConnection(17, 18);
        maze.addConnection(18, 17);
        maze.addConnection(18, 19);
        maze.addConnection(19, 18);
        maze.addConnection(19, 24);

        int startCell = 6;
        int endCell = 24;
        maze.traverse(startCell, endCell);
    }
}
