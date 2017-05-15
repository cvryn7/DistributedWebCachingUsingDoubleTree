package com.localserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main class.
 *
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/localserver/";
    public static volatile int count = 0;
    public static volatile int count_xyz = 0;
    private static ZooKeeper zk;
    private static ZkConnection zkcon;
    private static String zkConnectionString = "34.209.151.169";
    private static String zkPath = "/servers/servers";
    public static int region;
    public static List<ServerData> serverList;
    public static ServerHeap serverMaxPingHeap;
    public static Map<String, PartialDoubleTree> doubleTreeMap = new HashMap<String, PartialDoubleTree>();
    public static Map<String, String> hostMap = new HashMap<String, String>();
    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.localserver package
        final ResourceConfig rc = new ResourceConfig().packages("com.localserver");

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
    public static void main(String[] args) throws Exception {

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
            region = zkConfigs.getRegion(zkPath);
            serverList = zkConfigs.getServersInRegion(region, zkPath);
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

        serverMaxPingHeap = new ServerHeap(serverList);
        serverMaxPingHeap.printHeap();

      /*  String name = "xyz";
        if(!doubleTreeMap.containsKey(name)) {
            try {
                doubleTreeMap.put(name, new PartialDoubleTree(serverMaxPingHeap, name));
            } catch(FileNotFoundException fnfe) {
                //return fnfe.toString();
            } catch(IOException ioe) {
                //return ioe.toString();
            } catch(Exception e) {
                //return e.toString();
            }
        }

        List<Node> leafs = doubleTreeMap.get(name).getLeafs();

        for(Node n: leafs) {
            System.out.println("Leaf : " + n.getHostName());
        }*/

        /*Node root = doubleTreeMap.get(name).getRoot();
        SimpleNode simpleRoot = new SimpleNode();
        try {
            simpleRoot.convertToSimpleNode(root);
        } catch (Exception e) {
           // return e.getLocalizedMessage();
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            String nodeString = mapper.writeValueAsString(simpleRoot);
            //return nodeString;
        } catch (JsonProcessingException jpe) {
            //return jpe.getOriginalMessage() + " Problem is here";
        }

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://" + simpleRoot.getHostName() + ":8080/myapp/cache/setnode");
        String response = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_PLAIN)
                .post(Entity.json(simpleRoot), String.class);
        System.out.println(response);*/

        hostMap.put("website1", "ec2-34-209-234-222.us-west-2.compute.amazonaws.com");
        hostMap.put("website2", "ec2-54-191-107-143.us-west-2.compute.amazonaws.com");
        PartialDoubleTree ptweb1 = new PartialDoubleTree(serverMaxPingHeap, "website1");
        serverMaxPingHeap = new ServerHeap(serverList);
        PartialDoubleTree ptweb2 = new PartialDoubleTree(serverMaxPingHeap, "website2");
        doubleTreeMap.put("website1", ptweb1);
        doubleTreeMap.put("website2", ptweb2);
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();
    }
}

