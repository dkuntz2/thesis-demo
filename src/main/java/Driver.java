package co.kuntz.demo;

import co.kuntz.demo.server.WebServer;
import co.kuntz.sqliteEngine.core.*;//RemoteDataMapperServer;

import co.kuntz.demo.demogui.DemoGUI;

public class Driver {
    public static void main(String[] args) {
        String database = "datastore.db";

        WebServer server = new WebServer(database);
        new Thread(server).start();

        RemoteDataMapperServer dmServer = new RemoteDataMapperServer(database);
        new Thread(dmServer).start();

        try {
            Thread.sleep(500);
        } catch (Throwable t) {
            // meh.
        }
        new DemoGUI();
    }
}
