package works.hop.field.graph;

import java.util.List;

public class CycleDetection {

    public static void main(String[] args) {
        Vertex02<String> vA = new Vertex02<>("A");
        Vertex02<String> vB = new Vertex02<>("B");
        Vertex02<String> vC = new Vertex02<>("C");
        Vertex02<String> vD = new Vertex02<>("D");
        Vertex02<String> vE = new Vertex02<>("E");
        Vertex02<String> vF = new Vertex02<>("F");

        List<Vertex02<String>> graph = List.of(vA, vB, vC, vD, vE, vF);

        vF.addNeighbour(vA);
        vA.addNeighbour(vC);
        vC.addNeighbour(vB);
        vC.addNeighbour(vD);
        vA.addNeighbour(vE);
        vE.addNeighbour(vF);

        new CycleDetection().detectCycle(graph);
    }

    public void detectCycle(List<Vertex02<String>> graph) {
        for (Vertex02<String> v : graph) {
            if (!v.isBeingVisited()) {
                dfs(v);
            }
        }
    }

    private void dfs(Vertex02<String> vertex) {
        vertex.setBeingVisited(true);
        for (Vertex02<String> v : vertex.getNeighbours()) {
            if (v.isBeingVisited()) {
                System.out.println("Backward edge ... there's a cycle");
                return;
            }
            if(!v.isVisited()){
                v.setVisited(true);
                dfs(v);
            }
        }

        vertex.setBeingVisited(false);
        vertex.setVisited(true);
    }
}
