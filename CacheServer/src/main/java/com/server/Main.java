package com.server;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * Main class.
 *
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://0.0.0.0:8080/myapp/";
    private static ZooKeeper zk;
    private static ZkConnection zkcon;
    private static String zkConnectionString = "34.209.151.169";
    private static String zkPath = "/cacheservercount/servers";
    public static int pingTime;
    public static String publicDNS;
    public static boolean isTopRegion = false;
    public static List<String> hostDnsList = new ArrayList<String>();
    public static List<ServerData> serverList;
    public static Map<String,SimpleNode> nodeMap = new HashMap<>();
    public static boolean isRoot = false;
    public static Set<String> replicaSet = new HashSet<>();
    public static Map<String, String> hostMap = new HashMap<String, String>();
    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.server package
        final ResourceConfig rc = new ResourceConfig().packages("com.server");

        // uncomment the following line if you want to enable
        // support for JSON on the service (you also have to uncomment
        // dependency on jersey-media-json module in pom.xml)
        // --
        // rc.addBinder(org.glassfish.jersey.media.json.JsonJaxbBinder);

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader("src/main/resources/publicdns.txt"));
        publicDNS = br.readLine();
        System.out.println(publicDNS);
        zkcon = new ZkConnection(zkConnectionString);

        try {
            zk = zkcon.connect();
        } catch(IOException ioe) {
            System.out.println(ioe);
            System.exit(1);
        } catch(InterruptedException ie) {
            System.out.println(ie);
            System.exit(1);
        }

        ZKConfigs zkConfigs = new ZKConfigs(zk, zkcon);

        try {
            isTopRegion = zkConfigs.isTopRegion(zkPath, publicDNS);
            if (isTopRegion) {
                hostDnsList = zkConfigs.getHostDns("cacheservercount/Hosts");
            } else {
                serverList = zkConfigs.getServersInRegion(zkPath);
                System.out.println(serverList.size());
            }
        } catch (KeeperException ke) {
            System.out.println(ke);
            System.exit(1);
        } catch (InterruptedException ie) {
            System.out.println(ie);
            System.exit(1);
        } catch (NoConfigException nce) {
            System.out.print(nce);
            System.exit(1);
        }


        hostMap.put("website1", "ec2-34-209-234-222.us-west-2.compute.amazonaws.com");
        hostMap.put("website2", "ec2-54-191-107-143.us-west-2.compute.amazonaws.com");
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();
    }
}

