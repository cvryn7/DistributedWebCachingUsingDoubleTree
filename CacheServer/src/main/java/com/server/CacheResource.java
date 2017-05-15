package com.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Root resource (exposed at "myresource" path)
 */
@Path("cache")
public class CacheResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @POST
    @Path("/setnode")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt(SimpleNode node) {
        Main.nodeMap.put(node.getWebpageName(), node);
        return "success";
    }

    @POST
    @Path("/thisisroot")
    @Produces(MediaType.TEXT_PLAIN)
    public String thisIsRoot() {
        Main.isRoot = true;
        return "success";
    }

    @GET
    @Path("/fetch/{name}")
    @Produces(MediaType.TEXT_HTML)
    public String fetchPage(@PathParam("name") String name) {
        if (Main.isRoot) {
            URL url = null;
            try {
                url = new URL("http://ec2-34-209-234-222.us-west-2.compute.amazonaws.com:8080/myapp/website2");
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
                return ioe.getLocalizedMessage();
            }

            if (data.equals("")) {
                return "empty data";
            }
            return data;
        } else {
            URL url = null;
            try {
                url = new URL("http://" + Main.nodeMap.get(name).getUpParent() + ":8080/myapp/cache/fetch/" + name);
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
                return ioe.getLocalizedMessage();
            }

            if (data.equals("")) {
                return "empty data";
            }
            return data;
        }
    }

    @GET
    @Path("/replica/{name}")
    @Produces(MediaType.TEXT_HTML)
    public String getReplica(@PathParam("name") String name) {
        if (Main.isRoot) {
            URL url = null;
            try {
                url = new URL("http://ec2-34-209-234-222.us-west-2.compute.amazonaws.com:8080/myapp/website2");
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
                return ioe.getLocalizedMessage();
            }

            if (data.equals("")) {
                return "empty data";
            }
            return data;
        } else {
            URL url = null;
            try {
                url = new URL("http://" + Main.nodeMap.get(name).getUpParent() + ":8080/myapp/cache/fetch/" + name);
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
                return ioe.getLocalizedMessage();
            }

            if (data.equals("")) {
                return "empty data";
            }
            return data;
        }
    }

    @GET
    @Path("/data/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getData(@PathParam("name") String name) throws IOException {
        if (!Main.replicaSet.contains(name)) {
            if (Main.hostMap.containsKey(name)) {
                URL url = null;
                try {
                    url = new URL("http://" + Main.hostMap.get(name) + ":8080/myapp/" + name);
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

                }

                if (data.equals("")) {

                }
                String filePath = "src/main/resources/replicas/" + Main.hostMap.get(name) + ".html";
                File file = new File(filePath);

                file.createNewFile();
                PrintWriter fileWriter = new PrintWriter(filePath, "UTF-8");
                fileWriter.print(data);
                fileWriter.close();
                Main.replicaSet.add(name);
            } else {
                return "Not in map";
            }

        } else {
            return "Not in set";
        }
        return "done!";
    }
}
