package co.kuntz.demo.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.net.URI;

import co.kuntz.sqliteEngine.core.RemoteDataMapperServer;
import co.kuntz.demo.server.WebServer;
import co.kuntz.demo.demogui.DemoGUI;

public class ControllerGui extends JFrame implements ActionListener {
    public static final String DATABASE_NAME = "datastore.db";

    private RemoteDataMapperServer dmServer;
    private WebServer server;

    private boolean serverRunning, dmServerRunning;
    private JLabel serverStatus, dmServerStatus;
    private JButton toggleServer, toggleDmServer, makeGuiClient, openWebPage;

    public ControllerGui() {
        super("Demo Control Panel");
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);

        setLayout(new GridLayout(1, 3, 5, 5));

        // Web server
        serverRunning = false;
        server = new WebServer(DATABASE_NAME);

        JPanel serverPanel = new JPanel();
        serverPanel.setLayout(new GridLayout(3, 1, 5, 5));

        JPanel serverStatusPane = new JPanel();
        serverStatusPane.setLayout(new FlowLayout());
        serverStatusPane.add(new JLabel("Web Server Status: "));
        serverStatus = new JLabel(serverRunning ? "Running" : "Stopped");
        serverStatus.setForeground(serverRunning ? Color.green : Color.red);
        serverStatusPane.add(serverStatus);
        serverPanel.add(serverStatusPane);

        toggleServer = new JButton(serverRunning ? "Stop Web Server" : "Start Web Server");
        toggleServer.addActionListener(this);
        serverPanel.add(toggleServer);

        if (Desktop.isDesktopSupported()) {
            openWebPage = new JButton("Open Web Page");
            openWebPage.addActionListener(this);

            serverPanel.add(openWebPage);
        } else {
            openWebPage = null;
        }

        add(serverPanel);


        // Data Mapper Server
        dmServerRunning = false;
        dmServer = new RemoteDataMapperServer(DATABASE_NAME);

        JPanel dmPanel = new JPanel();
        dmPanel.setLayout(new GridLayout(2, 1, 5, 5));

        JPanel dmStatusPane = new JPanel();
        dmStatusPane.setLayout(new FlowLayout());
        dmStatusPane.add(new JLabel("Data Mapper Server Status: "));
        dmServerStatus = new JLabel(dmServerRunning ? "Running" : "Stopped");
        dmServerStatus.setForeground(dmServerRunning ? Color.green : Color.red);
        dmStatusPane.add(dmServerStatus);
        dmPanel.add(dmStatusPane);

        toggleDmServer = new JButton(dmServerRunning ? "Stop Data Mapper Server" : "Start Data Mapper Server");
        toggleDmServer.addActionListener(this);
        dmPanel.add(toggleDmServer);

        add(dmPanel);


        // GUI Client
        makeGuiClient = new JButton("Create new GUI Client");
        makeGuiClient.addActionListener(this);
        add(makeGuiClient);

        setVisible(true);
    }

    @Override public void actionPerformed(ActionEvent e) {
        if (toggleServer == e.getSource()) {
            if (serverRunning) {
                server.stop();
            } else {
                new Thread(server).start();
            }

            try {
                Thread.sleep(750);
            } catch (Throwable t) {
                // meh
            }

            serverRunning = !serverRunning;
            serverStatus.setText(serverRunning ? "Running" : "Stopped");
            serverStatus.setForeground(serverRunning ? Color.green : Color.red);
            toggleServer.setText(serverRunning ? "Stop Web Server" : "Start Web Server");
        } else if (toggleDmServer == e.getSource()) {
            if (dmServerRunning) {
                dmServer.stop();
            } else {
                new Thread(dmServer).start();
            }

            try {
                Thread.sleep(750);
            } catch (Throwable t) {
                // meh
            }

            dmServerRunning = !dmServerRunning;
            dmServerStatus.setText(dmServerRunning ? "Running" : "Stopped");
            dmServerStatus.setForeground(dmServerRunning ? Color.green : Color.red);
            toggleDmServer.setText(dmServerRunning ? "Stop Data Mapper Server" : "Start Data Mapper Server");
        } else if (makeGuiClient == e.getSource()) {
            new Thread(new Runnable() {
                @Override public void run() {
                    new DemoGUI();
                }
            }).start();
        } else if (openWebPage != null && openWebPage == e.getSource()) {
            if (serverRunning) {
                try {
                    Desktop.getDesktop().browse(new URI("http://localhost:8080/index.html"));
                } catch (Throwable t) {
                    throw new RuntimeException("OMGWTF!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Server isn't running.");
            }
        }
    }
}
