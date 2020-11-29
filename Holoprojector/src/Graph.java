
import java.util.*;

// Graph class
public class Graph {
    // node of adjacency list 
    static class Node {
        int label;
        List<Edge> edges;

        public Node(int label, List<Edge> Node_edges) {
            this.label = label;
            edges = new ArrayList<>();
            for (Edge e : Node_edges) {
                edges.add(e);
            }
        }
        public Node(int label, Edge... edges) {
            this.label = label;
            this.edges = new ArrayList<>();
            this.edges.addAll(Arrays.asList(edges));
        }

        Node(int label) {
            this.label = label;
        }

    }

    // define adjacency list
    List<Node> adj_list = new ArrayList<>();

    //Graph Constructor
    public Graph(List<Node> nodes) {
        // adjacency list memory allocation
        for (Node n : nodes) {
            adj_list.add(n);
        }
    }

    public Graph() {
    }

    // print adjacency list for the graph
    public static void printGraph(Graph graph) {


        System.out.println("The contents of the graph:");
        for (Node n : graph.adj_list) {
            for (Edge e : n.edges)
                System.out.print("Vertex:" + n.label + " ==> " + e.dest + " (" + e.weight + ")\t");
            System.out.println();
        }


    }
}

