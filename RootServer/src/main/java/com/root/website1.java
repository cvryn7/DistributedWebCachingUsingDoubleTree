package com.root;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Root resource (exposed at "myresource" path)
 */
@Path("website1")
public class website1 {
    private static String path = "src/main/Resources/";
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getIt() throws IOException {
        String filename = Main.filename;
        String dataString;
        try {
            dataString = new String(Files.readAllBytes(Paths.get(path + filename)));
        } catch (IOException ioe) {
            return ioe.toString();
        }
        return dataString;
    }
}
