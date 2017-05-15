package com.localserver;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import static com.localserver.Main.count_xyz;


/**
 * Root resource (exposed at "myresource" path)
 */
@Path("website2")
public class website2 {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getIt() {
        URL url = null;
        try {
            url = new URL("http://ec2-34-209-234-222.us-west-2.compute.amazonaws.com:8080/myapp/website2");
        } catch (MalformedURLException mue) {

        }
        if(count_xyz < 3) {
            count_xyz++;
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ie) {

            }
        }else {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ie) {

            }
        }
        String data = "";
        String temp = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            while (null != (temp = br.readLine())) {
                data = data + temp;
            }
        } catch (IOException ioe) {
            count_xyz = 0;
        }

        if(data.equals("")) {
            //count_xyz = 0;

        }
        return data;
    }
}
