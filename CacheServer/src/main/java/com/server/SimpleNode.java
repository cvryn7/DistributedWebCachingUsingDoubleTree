package com.server;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class SimpleNode {
    private String webpageName;
    private String hostName;
    private int ping;
    private String upParent;
    private String leftParent;
    private List<String> downChildren = new ArrayList<String>();
    private int numDownChildren = 0;
    private List<String> rightChildren = new ArrayList<String>();
    private int numRightchildren = 0;

    SimpleNode(){}

    SimpleNode(String hostName, int ping, String webpageName) {
        this.hostName = hostName;
        this.ping = ping;
        this.webpageName = webpageName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getWebpageName() {
        return webpageName;
    }

    public void setWebpageName(String webpageName) {
        this.webpageName = webpageName;
    }

    public int getPing() {
        return ping;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }

    public String getUpParent() {
        return upParent;
    }

    public void setUpParent(String upParent) {
        this.upParent = upParent;
    }

    public String getLeftParent() {
        return leftParent;
    }

    public void setLeftParent(String leftParent) {
        this.leftParent = leftParent;
    }

    public List<String> getDownChildren() {
        return downChildren;
    }

    public void setDownChildren(List<String> downChildren){
        this.downChildren = downChildren;
    }

    public List<String> getRightChildren() {
        return rightChildren;
    }

    public void setRightChildren(List<String> rightChildren) {
        this.rightChildren = rightChildren;
    }

    public int getNumDownChildren() {
        return numDownChildren;
    }

    public void setNumDownChildren(int numDownChildren) {
        this.numDownChildren = numDownChildren;
    }

    public int getNumRightchildren() {
        return numRightchildren;
    }

    public void setNumRightchildren(int numRightchildren) {
        this.numDownChildren = numRightchildren;
    }
    @JsonIgnore
    public void insertDownChild(String child) {
        downChildren.add(child);
        numDownChildren++;
    }

    @JsonIgnore
    public void insertRightChild(String child) {
        rightChildren.add(child);
        numRightchildren++;
    }

    @JsonIgnore
    public boolean isThereUpParent() {
        return upParent != null;
    }

    @JsonIgnore
    public boolean isThereLeftParent() {
        return leftParent != null;
    }

    @JsonIgnore
    public boolean isRoot() {
        return upParent == null;
    }

    @JsonIgnore
    public boolean isDownLeaf() {
        return downChildren.size() == 0;
    }

    @JsonIgnore
    public void convertToSimpleNode(Node node) {
        this.hostName = node.getHostName();
        this.webpageName = node.getWebpageName();
        this.ping = node.getPing();
        setUpParent(node.getUpParent() == null?null:node.getUpParent().getHostName());
        setLeftParent(node.getLeftParent()== null?null:node.getLeftParent().getHostName());
        for(Node n : node.getDownChildren()) {
            insertDownChild(n.getHostName());
        }
        for(Node n: node.getRightChildren()) {
            insertRightChild(n.getHostName());
        }
    }
}
