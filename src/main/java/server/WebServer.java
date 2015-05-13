package co.kuntz.demo.server;

import httpserver.*;
import com.google.gson.Gson;
import co.kuntz.demo.shared.User;

import co.kuntz.sqliteEngine.core.LocalDataMapper;

import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;

public class WebServer extends HttpServer {

    public static final String STATIC_FILES_DIRECTORY = "frontend";
    public static final String MESSAGE_404 = "404 - File Not Found";

    private LocalDataMapper dataMapper;

    private Gson gson = new Gson();

    public WebServer(String dataMapperName) {
        super(8080, "Demo Web Server", "0.0.1", "");
        dataMapper = new LocalDataMapper(dataMapperName);

        post(new Route("/addUser") {
            @Override public void handle(HttpRequest request, HttpResponse response) {
                String userString = request.getParam("user");
                User u = gson.fromJson(userString, User.class);

                response.setBody(Boolean.toString(dataMapper.put("users/" + u.getUsername(), u)));
            }
        });

        get(new Route("/getUsers") {
            @Override public void handle(HttpRequest request, HttpResponse response) {
                response.setBody(gson.toJson(dataMapper.startsWith("users/", User.class)));
            }
        });

        post(new Route("/addNote") {
            @Override public void handle(HttpRequest request, HttpResponse response) {
                String name = request.getParam("name");
                String text = request.getParam("text");

                System.out.println(request.getParams());

                response.setBody(Boolean.toString(dataMapper.put("notes/" + name, text)));
            }
        });

        get(new Route("/getNotes") {
            @Override public void handle(HttpRequest request, HttpResponse response) {
                response.setBody(gson.toJson(dataMapper.startsWith("notes/", String.class)));
            }
        });

        get(new Route("{*}") {
            @Override public void handle(HttpRequest request, HttpResponse response) {
                try {
                    // Create the path
                    StringBuilder pathBuilder = new StringBuilder();

                    // Add a '/' and part of our path
                    for (String segment : request.getSplitPath()) {
                        pathBuilder.append("/");
                        pathBuilder.append(segment);
                    }

                    // Set the path to the pathBuilder or a '/' if the path is empty.
                    String path = pathBuilder.toString();
                    if (path.isEmpty()) {
                        path = "/";
                    }

                    // If the path ends in a '/' append `playback.html`
                    if (path.substring(path.length() - 1).equals("/")) {
                        path += "index.html";
                    }

                    path = STATIC_FILES_DIRECTORY + path;

                    // check that file exists
                    File f;
                    try {
                        f = new File(getResource(path));
                        if (!f.exists()) {
                            throw new NullPointerException("No such file: " + path);
                        }
                    } catch (NullPointerException e) {
                        response.message(404, MESSAGE_404);
                        return;
                    }

                    response.setMimeType(getResponseType(f));

                    // Read the file
                    InputStream inputStream = ClassLoader
                        .getSystemResourceAsStream(path);

                    // If the file doesn't exist, tell the client.
                    if (inputStream == null) {
                        response.message(404, MESSAGE_404);
                        return;
                    }

                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(inputStream));
                    StringBuilder builder = new StringBuilder();

                    for (String line = bufferedReader.readLine(); line != null; line = bufferedReader
                            .readLine()) {
                        builder.append(line);
                        builder.append("\n");
                    }

                    bufferedReader.close();

                    // Set the response to the file's contents.
                    response.setBody(builder.toString());
                } catch (IOException e) {
                    throw new RuntimeException("File Not Found", e);
                }
            }
        });
    }

    public String getResponseType(File f) {
        try {
            String probeType = Files.probeContentType(f.toPath());
            if (probeType != null) {
                return probeType;
            }

        }
        catch (IOException e) {
            // whatevs ...
            // If we get an IOException here, we can just try manually...
        }

        String path = f.toString();

        if (path.substring(path.length() - 4).equalsIgnoreCase("html")) {
            return "text/html";
        } else if (path.substring(path.length() - 3).equalsIgnoreCase("css")) {
            return "text/css";
        } else if (path.substring(path.length() - 2).equalsIgnoreCase("js")) {
            return "text/javascript";
        } else if (path.substring(path.length() - 3).equalsIgnoreCase("png")) {
            return "image/png";
        } else if (path.substring(path.length() - 3).equalsIgnoreCase("jpg")) {
            return "image/jpg";
        } else if (path.substring(path.length() - 3).equalsIgnoreCase("svg")) {
            return "image/svg+xml";
        } else if (path.substring(path.length() - 3).equalsIgnoreCase("zip")) {
            return "application/zip";
        } else {
            return "text/plain";
        }
    }

    public static String getResource(String path) {
        try {
            return URLDecoder.decode(ClassLoader.getSystemResource(URLDecoder.decode(path, "UTF-8")).getPath(), "UTF-8");
        } catch (UnsupportedEncodingException e) {	// This shouldn't happen...
            e.printStackTrace();
        }

        return path;
    }
}
