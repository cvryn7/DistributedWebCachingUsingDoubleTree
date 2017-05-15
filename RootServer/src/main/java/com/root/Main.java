package com.root;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * Main class.
 *
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://0.0.0.0:8080/myapp/";
    public static int count = 0;

    private static ZooKeeper zk;
    private static ZkConnection zkcon;
    private static String zkConnectionString = "34.209.151.169";
    private static String zkPath = "/Host/datafiles";

    public static String filename;
    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.root package
        final ResourceConfig rc = new ResourceConfig().packages("com.root");

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
            filename = zkConfigs.getWebpageName(zkPath);
        } catch (KeeperException ke) {
            System.out.println(ke);
            System.exit(1);
        } catch (InterruptedException ie) {
            System.out.println(ie);
            System.exit(1);
        }
        System.out.println(filename);
        HttpServer server = null;
        try {
            server = startServer();
        } catch (Exception e) {
            System.out.println("exception!");
        }

        //Map<String, String> config = fetchConfig();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();
    }
}

