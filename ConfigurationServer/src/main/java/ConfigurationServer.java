import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ConfigurationServer {
    private static List<String> filesList = new ArrayList<String>();
    private static String filesPath = "src/main/resources/";
    private static String fileExt = ".yml";

    private static ZooKeeper zookeeper;
    private static ZkConnection zkcon;
    private static String zkConnectionString = "34.209.151.169";

    public static void main(String[] args) {
        ConfigurationServer cserver = new ConfigurationServer();
        filesList.add("cacheservercount");
        filesList.add("Host");
        filesList.add("servers");

        zkcon = new ZkConnection(zkConnectionString);
        try {
            zookeeper =  zkcon.connect();
        } catch(IOException ioe) {
            System.out.print(ioe);
        } catch(InterruptedException ie) {
            System.out.print(ie);
        }

        for(String filename : filesList) {
            cserver.readConfigFile(filename);
        }
    }

    public void readConfigFile(String filename) {
        Yaml yaml = new Yaml();
        InputStream inputFile = null;
        try {
            inputFile = new FileInputStream(new File(filesPath + filename + fileExt));
        } catch (FileNotFoundException fnfe) {
            System.out.println("file : " + filesPath + filename + fileExt + " not found!");
            System.exit(1);
        }

        Map<String, Object> dataMap = (Map<String, Object>)yaml.load(inputFile);

        pushDataToZk( "/" + filename,dataMap);

    }

    public void pushDataToZk(String path, Map<String, Object> dataMap) {
        createNode(path, "");
        for(String key: dataMap.keySet()) {
            if (dataMap.get(key) instanceof Integer) {
                createNode(path + "/" + key, Integer.toString((int)dataMap.get(key)));
            } else if (dataMap.get(key) instanceof String) {
                createNode(path + "/" + key, (String)dataMap.get(key));
            } else if (dataMap.get(key) instanceof List) {
                pushDataFromList(path + "/" + key,(List<Object>)dataMap.get(key));
            } else {
                pushDataToZk(path + "/" + key, (Map<String, Object>) dataMap.get(key));
            }
        }
    }

    public void pushDataFromList(String path, List<Object> objectList) {
        int count = 0;
        createNode(path, "");
        for(Object obj : objectList) {
            if (obj instanceof Integer) {
                createNode(path + "/" + count, Integer.toString((int)obj));
            } else if (obj instanceof String) {
                createNode(path + "/" + count, (String)obj);
            } else if (obj instanceof List) {
                pushDataFromList(path + "/" + count,(List<Object>)obj);
            } else {
                pushDataToZk(path + "/" + count, (Map<String, Object>)obj);
            }
            count++;
        }
    }

    public void createNode(String node, String data) {
        try {
            zkcon.create(zookeeper, node, data.getBytes());
        } catch (KeeperException ke) {
            System.out.println("Keeper execption adding node : " + node + " \n" + ke);
        } catch (InterruptedException ie) {
            System.out.println("InterruptedException adding node : " + node + " \n" +ie);
        }
    }
}
