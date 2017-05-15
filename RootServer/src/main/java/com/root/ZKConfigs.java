package com.root;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.List;


public class ZKConfigs {
    private ZooKeeper zk;
    private ZkConnection zkcon;
    ZKConfigs(ZooKeeper zk, ZkConnection zkcon) {
        this.zk = zk;
        this.zkcon = zkcon;
    }

    public String getWebpageName(String path) throws KeeperException, InterruptedException {
        Stat nodeStatus = exist(path);
        List<String> nodeChild;
        String filename = null;
        if (nodeStatus != null) {
            nodeChild = zk.getChildren(path, false);
            if (nodeChild.get(0).equals("name")) {
                if (nodeChild.get(1).equals("istaken")) {
                    if ((new String(getData(path + "/" + nodeChild.get(1))).equals("0"))) {
                        setData(path + "/" + nodeChild.get(1), "1");
                        return new String(getData(path + "/" + nodeChild.get(0)));
                    } else {
                        return null;
                    }
                }
            } else {

                for(String nodeName : nodeChild) {
                    filename = getWebpageName(path + "/" + nodeName);
                    if (filename != null)
                        break;
                }
            }
            return filename;
        } else {
            return null;
        }
    }

    private Stat exist(String path) throws KeeperException, InterruptedException {
        return zk.exists(path, false);
    }

    private byte[] getData(String path) throws KeeperException, InterruptedException {
        return zk.getData(path, null, null);
    }

    private void setData(String path, String data) throws KeeperException, InterruptedException {
        zk.setData(path, data.getBytes(), zk.exists(path, true).getVersion());
    }
}
