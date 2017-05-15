package com.root;// import java classes

import org.apache.zookeeper.*;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

// import zookeeper classes

public class ZkConnection {

    // declare zookeeper instance to access ZooKeeper ensemble
    private ZooKeeper zoo;
    final CountDownLatch connectedSignal = new CountDownLatch(1);
    private String zkConnectionString;
    private int zkPort = 2181;
    ZkConnection(String connString) {
        zkConnectionString = connString;
    }

    // Method to connect zookeeper ensemble.
    public ZooKeeper connect() throws IOException,InterruptedException {

        zoo = new ZooKeeper(zkConnectionString + ":" + zkPort,5000,new Watcher() {

            public void process(WatchedEvent we) {

                if (we.getState() == KeeperState.SyncConnected) {
                    connectedSignal.countDown();
                }
            }
        });

        connectedSignal.await();
        return zoo;
    }

    // Method to disconnect from zookeeper server
    public void close() throws InterruptedException {
        zoo.close();
    }

    public void create(ZooKeeper zk, String node, byte[] data) throws KeeperException, InterruptedException {
        zk.create(node, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }
}
