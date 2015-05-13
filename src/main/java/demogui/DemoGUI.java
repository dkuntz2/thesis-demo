package co.kuntz.demo.demogui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import co.kuntz.sqliteEngine.core.RemoteCachedDataMapper;
import co.kuntz.demo.shared.User;

import java.util.Map;

public class DemoGUI extends JFrame {
    JTabbedPane userTabs, noteTabs;

    JTextField inputUsername, inputEmail, inputRealName, inputNoteName, inputNoteText;
    JButton addUser, addNote;

    RemoteCachedDataMapper dataMapper;

    public DemoGUI() {
        super("Remote Data Mapper Demo");

        dataMapper = new RemoteCachedDataMapper("cached.db");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setDefaultLookAndFeelDecorated(true);
        setSize(600, 350);

        JTabbedPane main = new JTabbedPane();
        add(main);

        JPanel usersPanel = new JPanel();
        usersPanel.setLayout(new GridLayout(1, 2, 5, 5));

        userTabs = new JTabbedPane(JTabbedPane.LEFT);
        usersPanel.add(userTabs);
        userTabs.addTab("THIS", new JLabel("what"));

        getUsers();

        inputUsername = new JTextField();
        inputEmail = new JTextField();
        inputRealName = new JTextField();

        JPanel makeUser = new JPanel();
        makeUser.setLayout(new GridLayout(2, 1, 5, 5));

        JPanel userInfo = new JPanel();
        userInfo.setLayout(new GridLayout(3, 2, 5, 5));
        userInfo.add(new JLabel("Username"));
        userInfo.add(inputUsername);
        userInfo.add(new JLabel("Email Address"));
        userInfo.add(inputEmail);
        userInfo.add(new JLabel("Real Name"));
        userInfo.add(inputRealName);
        makeUser.add(userInfo);

        addUser = new JButton("Submit");
        addUser.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                String username = inputUsername.getText();
                String email = inputEmail.getText();
                String realName = inputRealName.getText();

                User u = new User(username, realName, email);
                dataMapper.put("users/" + username, u);

                inputUsername.setText("");
                inputEmail.setText("");
                inputRealName.setText("");
                getUsers();
            }
        });

        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());
        buttons.add(addUser);
        makeUser.add(buttons);

        usersPanel.add(makeUser);

        main.addTab("Users", usersPanel);

        JPanel notesPanel = new JPanel();
        notesPanel.setLayout(new GridLayout(1, 2, 5, 5));

        noteTabs = new JTabbedPane(JTabbedPane.LEFT);
        notesPanel.add(noteTabs);

        getNotes();

        inputNoteName = new JTextField();
        inputNoteText = new JTextField();

        JPanel makeNote = new JPanel();
        makeNote.setLayout(new GridLayout(2, 1, 5, 5));

        JPanel noteInfo = new JPanel();
        noteInfo.setLayout(new GridLayout(2, 2, 5, 5));
        noteInfo.add(new JLabel("Title"));
        noteInfo.add(inputNoteName);
        noteInfo.add(new JLabel("Text"));
        noteInfo.add(inputNoteText);
        makeNote.add(noteInfo);

        addNote = new JButton("Submit");
        addNote.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                String title = inputNoteName.getText();
                String text = inputNoteText.getText();

                dataMapper.put("notes/" + title, text);

                inputNoteName.setText("");
                inputNoteText.setText("");
                getNotes();
            }
        });

        JPanel nbuttons = new JPanel();
        nbuttons.setLayout(new FlowLayout());
        nbuttons.add(addNote);
        makeNote.add(nbuttons);

        notesPanel.add(makeNote);
        main.addTab("Notes", notesPanel);

        setVisible(true);
    }


    public void getUsers() {
        userTabs.removeAll();

        Map<String, User> users = (Map<String, User>) (Map) dataMapper.startsWith("users/", User.class);
        for (User user : users.values()) {
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(2, 1, 5, 5));
            panel.add(new JLabel("<html><center><h2>" + user.getUsername() + "</h2></center></html>", SwingConstants.CENTER));

            JPanel info = new JPanel();
            info.setLayout(new GridLayout(3, 2, 5, 5));
            info.add(new JLabel("<html><strong>Username</strong></html>"));
            info.add(new JLabel(user.getUsername()));
            info.add(new JLabel("<html><strong>Email Address</strong></html>"));
            info.add(new JLabel(user.getEmailAddress()));
            info.add(new JLabel("<html><strong>Real Name</strong></html>"));
            info.add(new JLabel(user.getRealName()));

            panel.add(info);

            userTabs.addTab(user.getUsername(), panel);
        }
    }

    public void getNotes() {
        noteTabs.removeAll();

        Map<String, String> notes = (Map<String, String>) (Map) dataMapper.startsWith("notes", String.class);
        for (Map.Entry<String, String> note : notes.entrySet()) {
            String noteTitle = note.getKey().substring(6);
            String noteText = note.getValue();

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(2, 1, 5, 5));
            panel.add(new JLabel("<html><center><h2>" + noteTitle + "</h2></center></html>", SwingConstants.CENTER));
            panel.add(new JLabel(noteText));

            noteTabs.addTab(noteTitle, panel);
        }
    }
}
