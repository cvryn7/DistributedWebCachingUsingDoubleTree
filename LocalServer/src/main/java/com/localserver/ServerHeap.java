package com.localserver;

import java.util.List;


public class ServerHeap {
    private ServerData[] heap;
    private int size = 0;
    ServerHeap(List<ServerData> serverData) {
        heap = new ServerData[serverData.size()];
        for(ServerData s: serverData) {
            insert(s);
        }
    }

    public void insert(ServerData serverData) {
        heap[size++] = serverData;
        heapify(size-1);
    }

    public void heapify(int childNode) {
        ServerData temp = heap[childNode];
        while(childNode > 0 && temp.getPingTime() > heap[parentNode(childNode)].getPingTime()) {
            heap[childNode] = heap[parentNode(childNode)];
            childNode = parentNode(childNode);
        }
        heap[childNode] = temp;
    }

    public int parentNode(int childNode) {
        return (childNode-1)/2;
    }

    public ServerData popRoot() {
        ServerData root;
        if (!isEmpty()) {
            root = heap[0];
            heap[0] = heap[size-1];
            size--;
            heapifyDown(0);
            return root;
        } else {
            return null;
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void heapifyDown(int node) {
        int minChild;
        ServerData temp = heap[node];

        while( 2*node + 1 < size) {
            minChild = findMinChild(node);
            if(heap[minChild].getPingTime() > temp.getPingTime()) {
                heap[node] = heap[minChild];
            } else {
                break;
            }
            node = minChild;
        }
        heap[node] = temp;
    }

    public int findMinChild(int node) {
        if (2*node + 2 <= size) {
            if (heap[2*node+1].getPingTime() > heap[2*node+2].getPingTime()) {
                return 2*node+1;
            } else {
                return 2*node+2;
            }
        } else {
            return 2*node + 1;
        }
    }

    public void printHeap() {
        System.out.println("Server Ping Max Heap");
        for(int i = 0; i < size; i++) {
            System.out.println("NAME: " + heap[i].getHostName() + " PING: " + heap[i].getPingTime());
        }
    }

    public void printSorted() {
        System.out.println("sorted print");
        int actualHeapSize = size;
        for(int i=0; i < actualHeapSize; i++) {
            ServerData data = popRoot();
            System.out.println("NAME: " + data.getHostName() + " PING: " + data.getPingTime());
        }
    }

    public int getSize() {
        return size;
    }
}
