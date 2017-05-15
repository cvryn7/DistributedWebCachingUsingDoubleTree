package com.server;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class RegionConfig {
    private int regionNumber;
    private int takeCount;
    private String path;

    RegionConfig(int regionNumber, int takeCount, String path) {
        this.regionNumber = regionNumber;
        this.takeCount = takeCount;
        this.path = path;
    }

    public int getRegionNumber() {
        return regionNumber;
    }

    public void setRegionNumber(int regionNumber) {
        this.regionNumber = regionNumber;
    }

    public int getTakecount() {
        return takeCount;
    }

    public void setTakeCount(int takeCount) {
        this.takeCount = takeCount;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}

public class ZKConfigs {
    private ZooKeeper zk;
    private ZkConnection zkcon;
    private String foundRegionNumber;
    ZKConfigs(ZooKeeper zk, ZkConnection zkcon) {
        this.zk = zk;
        this.zkcon = zkcon;
    }

    public boolean isTopRegion(String path, String publicDns) throws KeeperException, InterruptedException, NoConfigException {
        Stat nodeStatus = exist(path);
        List<String> nodeChild;

        int regionNum;
        if(nodeStatus != null) {
            nodeChild = zk.getChildren(path, false);
            for (String s: nodeChild) {
                String regionNumber = new String(getData(path + "/" + s + "/region"));
                String regionTakeCount = new String(getData(path + "/" + s + "/takecount"));
                List<String> regionServers = zk.getChildren(path + "/" + s + "/servers", false);
                for (String serverNum : regionServers) {
                    String name = new String(getData(path + "/" + s + "/servers/" + serverNum + "/name"));
                    if (name.equals(publicDns) && regionTakeCount.equals(-1)) {
                        foundRegionNumber = regionNumber;
                        return true;
                    } else if (name.equals(publicDns)) {
                        foundRegionNumber = regionNumber;
                        return false;
                    }
                }
            }
        }
        throw new NoConfigException("No configuration found");
    }

    public List<String> getHostDns(String path) throws KeeperException, InterruptedException, NoConfigException {
        Stat nodeStatus = exist(path);
        List<String> nodeChild;
        List<String> hostDns = new ArrayList<>();

        if (nodeStatus != null) {
            nodeChild = zk.getChildren(path, false);
            for(String s: nodeChild) {
                String dnsName = new String(getData(path + "/" + s + "/name"));
                hostDns.add(dnsName);
            }
            return hostDns;
        }
        throw new NoConfigException("No Host DNS found");
    }

    public int getRegion(String path) throws KeeperException, InterruptedException, NoConfigException {
        Stat nodeStatus = exist(path);
        List<String> nodeChild;
        int regionNum;
        if (nodeStatus != null) {
            nodeChild = zk.getChildren(path, false);
            List<RegionConfig> regionList = new ArrayList<RegionConfig>();
            for(String s: nodeChild) {

            }

            int min = 999999;
            RegionConfig minTakeCountRegion = null;

            for(RegionConfig rc: regionList) {
                if (rc.getTakecount() < min && rc.getTakecount() != -1) {
                    min = rc.getTakecount();
                    minTakeCountRegion = rc;
                }
            }

            regionNum = minTakeCountRegion.getRegionNumber();
            setData(minTakeCountRegion.getPath(), Integer.toString(minTakeCountRegion.getTakecount() + 1));
            return regionNum;
        } else {
            throw new NoConfigException("No Configuration Found");
        }
    }

    public List<ServerData> getServersInRegion(String path) throws KeeperException, InterruptedException, NoConfigException{
        Stat nodeStatus = exist(path);
        List<String> nodeChild;

        if (nodeStatus != null) {
            List<ServerData> serverList = new ArrayList<ServerData>();
            nodeChild = zk.getChildren(path, false);
            String region = null;
            for(String node: nodeChild) {
                region = new String(getData(path + "/" + node + "/region"));
                String takecount = new String(getData(path + "/" + node + "/takecount"));
                if (takecount.equals("-1")) {
                    break;
                }
            }
            String regionPath = path + "/" + region + "/servers";
            nodeChild = zk.getChildren(regionPath, false);
            for(String s: nodeChild) {
                String hostName = new String(getData(regionPath + "/" + s + "/name"));
                String port = new String(getData(regionPath + "/" + s + "/port"));
                String pingTime = new String(getData(regionPath + "/" + s + "/pingtime"));

                ServerData serverData = new ServerData(hostName, Integer.parseInt(pingTime), Integer.parseInt(port));
                serverList.add(serverData);
            }
            return serverList;
        } else {
            throw new NoConfigException("No server list config found");
        }
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
