package com.localserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Random;

import static com.localserver.Main.doubleTreeMap;
import static com.localserver.Main.serverMaxPingHeap;


/**
 * Root resource (exposed at "myresource" path)
 */
@Path("webpage")
public class WebpageResource {
    private Client client = null;
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("{name}")
    public String getIt(@PathParam("name") String name) {


        if(!doubleTreeMap.containsKey(name)) {
            try {
                doubleTreeMap.put(name, new PartialDoubleTree(serverMaxPingHeap, name));
            } catch(FileNotFoundException fnfe) {
                return fnfe.toString();
            } catch(IOException ioe) {
                return ioe.toString();
            } catch(Exception e) {
                return e.toString();
            }
            PartialDoubleTree pdt = doubleTreeMap.get(name);
            populateCaches(pdt.getRoot(), true);
        }



        List<Node> leafs = Main.doubleTreeMap.get(name).getLeafs();
        Random rand = new Random();
        int index = rand.nextInt(leafs.size()-1);

        String hostName = leafs.get(index).getHostName();
        URL url = null;
        try {
            url = new URL("http://ec2-34-209-234-222.us-west-2.compute.amazonaws.com:8080/myapp/website1");
        } catch (MalformedURLException mue) {

        }
        String temp = "";
        String data = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            while (null != (temp = br.readLine())) {
                data = data + temp;
            }
        } catch (IOException ioe) {
        }
        if(data.equals("")) {
            //count = 0;

        }
        try {
            url = new URL("http://" + hostName + ":8080/myapp/cache/fetch/" + name);
        } catch (MalformedURLException mue) {

        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            while (null != (temp = br.readLine())) {
                data = data + temp;
            }
        } catch (IOException ioe) {
            return ioe.getLocalizedMessage();
        }

        if (data.equals("")) {
            return "empty data";
        }
        return data;
    }

    public void populateCaches(Node node, boolean isRoot) {
        if (node == null) {
            return;
        }
        SimpleNode simpleRoot = new SimpleNode();
        try {
            simpleRoot.convertToSimpleNode(node);
        } catch (Exception e) {
            // return e.getLocalizedMessage();
        }

        if (client == null) {
            client = ClientBuilder.newClient();
        }
        WebTarget target = client.target("http://" + simpleRoot.getHostName() + ":8080/myapp/cache/setnode");
        String response = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_PLAIN)
                .post(Entity.json(simpleRoot), String.class);
        System.out.println(response);
        if (isRoot) {
            URL url = null;
            try {
                url = new URL("http://" + simpleRoot.getHostName() + ":8080/myapp/cache/thisisroot");
            } catch (MalformedURLException mue) {

            }
            String data = "";
            String temp = "";
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                while (null != (temp = br.readLine())) {
                    data = data + temp;
                }
            } catch (IOException ioe) {
                ioe.getLocalizedMessage();
            }
        }
        for(int i = 0; i < 2; i++) {
            //populateCaches(node.getDownChildren().size() > 0? node.getDownChildren().get(0): null, false);
            //populateCaches(node.getDownChildren().size() > 1? node.getDownChildren().get(0): null, false);
        }
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/get/{name}")
    public String getWebpage(@PathParam("name") String name) {
        String data = "";
        if (Main.hostMap.containsKey(name)) {
            List<Node> leafs = Main.doubleTreeMap.get(name).getLeafs();
            Random rand = new Random();
            int index = rand.nextInt(leafs.size()-1);

            Node cacheNode = leafs.get(index);
            do {
                URL url = null;
                try {
                    url = new URL("http://" + cacheNode.getHostName() + ":8080/myapp/cache/data/"+name);
                } catch (MalformedURLException mue) {

                }
                String temp = "";
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                    while (null != (temp = br.readLine())) {
                        data = data + temp;
                    }
                } catch (IOException ioe) {
                    ioe.getLocalizedMessage();
                }
                cacheNode = cacheNode.getUpParent();
            } while (cacheNode != null);
            URL url = null;
            try {
                url = new URL("http://" + Main.hostMap.get(name) + ":8080/myapp/website1");
            } catch (MalformedURLException mue) {

            }
            String temp = "";
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                while (null != (temp = br.readLine())) {
                    data = data + temp;
                }
            } catch (IOException ioe) {
                ioe.getLocalizedMessage();
            }
        }
        return data;
    }
}
