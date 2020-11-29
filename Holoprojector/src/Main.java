import java.util.*;

public class Main {
    //get int label from char, to make it easier on myself to construct the graph
    static int c(char c){
        switch (c){
            case 'a': return 0;
            case 'b': return 1;
            case 'c': return 2;
            case 'd': return 3;
            case 'e': return 4;
            case 'f': return 5;
            case 'g': return 6;
            case 'h': return 7;
            case 'i': return 8;
        }
        throw new RuntimeException("not good");
    }
    public static void main(String[] args) {
        // define edges of the graph
        Graph.Node A = new Graph.Node(0,
                new Edge(0, 1, 7), new Edge(0, 4, 12),
                new Edge(0, 6, 2));

        Graph.Node B = new Graph.Node(1,
                new Edge(1, 0, 7), new Edge(1, 6, 4),
                new Edge(1, 2, 6), new Edge(1, 4, 1));

        Graph.Node C = new Graph.Node(2,
                new Edge(2, 1, 6), new Edge(2, 3, 7),
                new Edge(2, 6,3));

        Graph.Node D = new Graph.Node(3,
                new Edge(3, c('c'), 7), new Edge(3, c('e'), 3),
                new Edge(3, c('h'), 4), new Edge(3, c('i'), 5),
                new Edge(3, c('f'), 9));

        Graph.Node E = new Graph.Node(4,
                new Edge(4, c('a'), 12), new Edge(4, c('b'), 1),
                new Edge(4, c('g'), 5), new Edge(4, c('d'), 3),
                new Edge(4, c('h'), 8), new Edge(4, c('i'), 2));

        Graph.Node F = new Graph.Node(5,
                new Edge(5, c('d'), 9), new Edge(5, c('i'), 13));

        Graph.Node G = new Graph.Node(6,
                new Edge(6, c('a'), 2), new Edge(6, c('b'), 4),
                new Edge(6, c('c'), 3), new Edge(6, c('e'), 5),
                new Edge(6, c('h'), 8));

        Graph.Node H = new Graph.Node(7,
                new Edge(7, c('g'), 8), new Edge(7, c('e'), 8),
                new Edge(7,c('d'), 4), new Edge(7, c('i'), 1));
        Graph.Node I = new Graph.Node(8,
                new Edge(8, c('f'), 13), new Edge(8, c('d'), 5),
                new Edge(8, c('e'), 2), new Edge(8, c('h'), 1));
        List<Graph.Node> nodes = new ArrayList<>();
        nodes.add(A);
        nodes.add(B);
        nodes.add(C);
        nodes.add(D);
        nodes.add(E);
        nodes.add(F);
        nodes.add(G);
        nodes.add(H);
        nodes.add(I);
        // call graph class Constructor to construct a graph
        Graph graph = new Graph(nodes);

        // print the graph as an adjacency list
        Graph.printGraph(graph);

        ArrayList<Graph.Node> visitedNodes = new ArrayList<>();
        //Initialize with an arbitrary node
        visitedNodes.add(nodes.get(0));
        System.out.println("Printing MST: ");
        while(visitedNodes.size() != nodes.size()){
            Edge minEdge = null;
            int minEdgeWeight = Integer.MAX_VALUE;
            Graph.Node minDestNode = null;
            Graph.Node minSrcNode = null;
            //Select lowest edge out of our active nodes
            for (Graph.Node activeNode : visitedNodes) {
                for (Edge edge : activeNode.edges) {
                    Graph.Node destNode = nodes.get(edge.dest);
                    if(!visitedNodes.contains(destNode)){
                        if(edge.weight < minEdgeWeight){
                            minEdge = edge;
                            minEdgeWeight = edge.weight;
                            minDestNode = destNode;
                            minSrcNode = activeNode;
                        }
                    }
                }
            }
            assert minEdge != null : "No edge found";
            assert minSrcNode != null : "No src node found";
            assert minDestNode != null : "No dest node found";
            visitedNodes.add(minDestNode);
            System.out.println("Node: " + minSrcNode.label + " ==> " + minDestNode.label);

        }
    }
}
 
