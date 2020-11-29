import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jake on 11/24/2020.
 * <insert description here>
 */
public class HuffmanEncoding {
    public static void main(String[] args) {
        System.out.println("After 19 years as a prisoner, Jean Valjean (Hugh Jackman) is freed by Javert (Russell Crowe), the officer in charge of the prison workforce.".length());
        String str = "1001000111100011101011101101001001100101011000110101010111110000111010110001" +
                "1101011110011100111010101000100101111101011100111011100000001010111111111010" +
                "0101110110000110011000101011111111100111101001101011111100000011001100000010" +
                "1101101100001100111010111111110001011010101000111011110111001001010011111101" +
                "0100000011011100000010111010001010111000111110011110101001001111100010001010" +
                "0000100001110101001111100010100011010100010011101110001110110001011000101111" +
                "0111101010101101010111011010101111111100110101100101111101000000101100010111" +
                "1011000111011000101100111001110101010001001011111110100011001011101000011111" +
                "00010111001101010001100";
        System.out.println(str.length());
    }
    static void encode(String str){
        //First, count the frequencies of each character, put it into a HashMap for simplicity
        HashMap<Character, Integer> characterCounts = new HashMap<>();
        for (char c : str.toCharArray()) {
            //set Integer to 0 if it was null (computeIfAbsent)
            Integer oldValue = characterCounts.computeIfAbsent(c, x -> 0);
            //Increment by 1
            characterCounts.put(c, oldValue + 1);
        }
        //Second, we need to generate the optimal encoding tree
        //A list of the active nodes that we will be comparing against eachother
        ArrayList<Node> activeNodes = new ArrayList<>();
        //Add the starting nodes:
        for (Map.Entry<Character, Integer> entry : characterCounts.entrySet()) {
            Node node = new Node();
            node.startingNode = true;
            node.charValue = entry.getKey();
            node.value = entry.getValue();
            activeNodes.add(node);
        }
        //Keep comparing nodes until we are left with a single node (the origin)
        while (activeNodes.size() > 1){
            //Assign nodes to some meaningless values, they will be updated later in the for loop
            Node lowestA = activeNodes.get(0);
            //Find the lowest node and assign it to A
            for (Node node : activeNodes) {
                if(node.value < lowestA.value){
                    lowestA = node;
                }
            }
            //Find lowest value that is not lowestA
            Node lowestB = activeNodes.get(0);
            //Make sure we dont assign lowestB and lowestA to the same thing
            if(lowestB == lowestA) lowestB = activeNodes.get(1);
            for (Node node : activeNodes) {
                if (node.value < lowestB.value) {
                    if(lowestA != node) {
                        lowestB = node;
                    }
                }
            }
            //Now that we have found the lowest 2 values of the active nodes, remove them from the active list and create a new node.
            Node newNode = new Node();
            //Also connect the new node to the old ones.
            newNode.left = lowestA;
            newNode.right = lowestB;
            newNode.value = lowestA.value + lowestB.value;
            activeNodes.add(newNode);
            activeNodes.remove(lowestA);
            activeNodes.remove(lowestB);
        }
        //Now we have constructed our tree with originNode as the origin, we need to traverse downward until we hit a number
        Node originNode = activeNodes.get(0);
        HashMap<Character, String> table = new HashMap<>();
        getCodings(originNode, "", table);

        //Finally, we have our table, replace all characters with their encoding.
        StringBuilder result = new StringBuilder();
        for (char c : str.toCharArray()) {
            String s = table.get(c);
            result.append(s);
        }
        //Print out table
        System.out.println("=========================");
        for (Map.Entry<Character, String> entry : table.entrySet()) {
            System.out.println("["+entry.getKey()+"] -> " + entry.getValue());
        }
        System.out.println("=========================");
        System.out.println("Result: " + result.toString());
    }
    private static void getCodings(Node currentNode, String current, HashMap<Character, String> results){
        //We want to recursively traverse through every node, then when we hit a starting node, add it to an array

        //Base case
        if(currentNode.startingNode){
            //break out of recursion, return a result.
            results.put(currentNode.charValue, current);
            return;
        }

        //If our currentNode is not a starting node, it must be connected to 2 other nodes
        //Add 0 to current string if left, 1 if right.
        getCodings(currentNode.left, current + "0", results);
        getCodings(currentNode.right, current + "1", results);
    }
}

/**
 * A node in a tree
 */
class Node{
    //The 2 lower nodes this node is connected to
    Node left;
    Node right;

    //The value of the node
    int value;

    //If the value is a starting node, and has a character assigned to it
    boolean startingNode = false;
    char charValue;
}
