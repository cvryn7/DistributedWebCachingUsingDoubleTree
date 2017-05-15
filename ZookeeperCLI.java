package com.root;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.data.Stat;

public class ZookeeperCLI {
    String connectionString = "ec2-34-209-151-169.us-west-2.compute.amazonaws.com";
    private Zookeeper zoo;
    private String path = "config/cacheservercount";
    final CountDownLatch connectedsig = new CountDownLatch(1);

    public Map<String, String> fetchConfig(){
        createConnection();
        exists(path);
        Map<String, String> config = getConfig(path);
        return config;
    }

    private void createConnection() {
        zoo = new ZooKeeper(connectionString,2181,new Watcher() {

            public void process(WatchedEvent we) {

                if (we.getState() == KeeperState.SyncConnected) {
                    connectedSig.countDown();
                }
            }
        });

        connectedSignal.await();
    }

    private Stat exists(String path) throws KeeperException, InterruptedException {
        return zoo.exists(path, true);
    }

    private Map<String, String> getConfig(String path) {
        byte[] b = zoo.getData(path, new watcher());
        int count = Integer.parseInt(new String(b, "UTF-8"));
        Map<String, String> dataMap = new HashMap<String, String>();

        for (int i = 0; i < count; i++) {
            byte[] isTakenByte = zoo.getData(path + "/" +  count + "/taken");
            int taken = Integer.parseInt(new String(isTakenByte, "UTF-8"));
            if(taken == 0) {
                byte[] nameByte = zoo.getData(path + "/name");
                byte[] portByte = zoo.getData(path + "/port");
                byte[] pingByte = zoo.getData(path + "/pingTime");
            }
        }

        return dataMap;
    }

}