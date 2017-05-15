package com.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;

public class PartialDoubleTree {
    Node root = null;
    private String webpageName;
    private ServerHeap heap;
    private Node[] replicationTreeHelperArray = null;
    private int replicationArraySizeIncrementer = 0;

    PartialDoubleTree(ServerHeap heap, String webpageName) throws IOException, FileNotFoundException{
        this.heap = heap;
        this.webpageName = webpageName;
        replicationTreeHelperArray = new Node[heap.getSize()];
        root = constructLookupTree(root, null);
        constructReplicationTree(replicationTreeHelperArray[0], null, 0);
        saveReplicationTree();
    }

    public Node constructLookupTree(Node node, Node parent) {
        ServerData sData = heap.popRoot();
        node = new Node(sData.getHostName(), sData.getPingTime(), webpageName);
        node.setUpParent(parent);
        replicationTreeHelperArray[replicationArraySizeIncrementer++] = node;

        Queue<Node> nodeQueue = new LinkedList<Node>();
        parent = node;
        do {
            Node child = null;
            ServerData childServerData = null;
            for(int i = 0; i < 2; i++) {
                if (!heap.isEmpty()) {
                    childServerData = heap.popRoot();
                    child = new Node(childServerData.getHostName(), childServerData.getPingTime(), webpageName);
                    child.setUpParent(parent);
                    parent.insertDownChild(child);
                    replicationTreeHelperArray[replicationArraySizeIncrementer++] = child;
                    nodeQueue.add(child);
                } else {
                    break;
                }
            }
            parent = nodeQueue.poll();
        } while (!nodeQueue.isEmpty() && !heap.isEmpty());
        return node;
    }

    public void constructReplicationTree(Node node, Node parent, int index) {
        node.setLeftParent(parent);
        int incrementer = 0;
        int arrayLen = replicationTreeHelperArray.length;
        for(int i = 0; i < arrayLen; i++) {
            node = replicationTreeHelperArray[i];
            if (i+1+incrementer < arrayLen) {
                replicationTreeHelperArray[i+1+incrementer].setLeftParent(node);
                node.insertRightChild(replicationTreeHelperArray[i+1+incrementer]);
            }
            if (i+2+incrementer < arrayLen) {
                replicationTreeHelperArray[i+2+incrementer].setLeftParent(node);
                node.insertRightChild(replicationTreeHelperArray[i+2+incrementer]);
            }
            incrementer += 1;
        }
    }

    public void saveReplicationTree() throws IOException, FileNotFoundException {
        String filePath = "src/main/ReplicationTreeStructures/" + webpageName + ".txt";
        File file = new File(filePath);

        file.createNewFile();
        PrintWriter fileWriter = new PrintWriter(filePath, "UTF-8");
        int arrayLen = replicationTreeHelperArray.length;
        for(int i = 0; i < arrayLen; i++) {
            fileWriter.print(replicationTreeHelperArray[i].getHostName() + " -->  ");
            if(replicationTreeHelperArray[i].getRightChildren().size() > 0) {
                fileWriter.print(replicationTreeHelperArray[i].getRightChildren().get(0).getHostName());
            }
            if(replicationTreeHelperArray[i].getRightChildren().size() > 1) {
                fileWriter.print( ", " + replicationTreeHelperArray[i].getRightChildren().get(1).getHostName());
            }
            fileWriter.print("\n");
        }
        fileWriter.close();
    }

    public Node getRoot() {
        return root;
    }
}
