package com.server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cv on 5/4/2017.
 */
class Node {
    private String webpageName;
    private String hostName;
    private int ping;
    private Node upParent;
    private Node leftParent;
    private List<Node> downChildren = new ArrayList<Node>();
    private int numDownChildren = 0;
    private List<Node> rightChildren = new ArrayList<Node>();
    private int numRightchildren = 0;

    Node(String hostName, int ping, String webpageName) {
        this.hostName = hostName;
        this.ping = ping;
        this.webpageName = webpageName;
    }

    public void insertDownChild(Node child) {
        downChildren.add(child);
        numDownChildren++;
    }

    public void insertRightChild(Node child) {
        rightChildren.add(child);
        numRightchildren++;
    }

    public List<Node> getDownChildren() {
        return downChildren;
    }

    public List<Node> getRightChildren() {
        return rightChildren;
    }

    public Node getUpParent() {
        return upParent;
    }

    public Node getLeftParent() {
        return leftParent;
    }

    public void setUpParent(Node upParent) {
        this.upParent = upParent;
    }

    public void setLeftParent(Node leftParent) {
        this.leftParent = leftParent;
    }

    public boolean isThereUpParent() {
        return upParent != null;
    }

    public boolean isThereLeftParent() {
        return leftParent != null;
    }

    public boolean isRoot() {
        return upParent == null;
    }

    public boolean isDownLeaf() {
        return downChildren.size() == 0;
    }

    public String getHostName() {
        return hostName;
    }

    public String getWebpageName() {
        return webpageName;
    }

    public int getPing() {
        return ping;
    }
}