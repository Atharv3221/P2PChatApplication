package p2pchatapplication;
import fi.iki.elonen.NanoHTTPD;

import java.io.*;

public class LocalWebServer extends NanoHTTPD {

    private static final String BASE_PATH = "web"; // e.g., folder where your HTML/CSS/JS files are

    public LocalWebServer() throws IOException {
        super(8080);
        start(SOCKET_READ_TIMEOUT, false);
        System.out.println("Server started on http://localhost:8080");
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if (uri.equals("/")) {
            uri = "/index.html"; // default file
        }

        File file = new File(BASE_PATH + uri);
        if (!file.exists() || file.isDirectory()) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "404 Not Found");
        }

        try {
            String mime = getMimeTypeForFile(uri);
            return newChunkedResponse(Response.Status.OK, mime, new FileInputStream(file));
        } catch (IOException e) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "500 Internal Server Error");
        }
    }
}