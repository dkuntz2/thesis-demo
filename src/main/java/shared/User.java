package co.kuntz.demo.shared;

public class User {
    private final String username;
    private final String realName;
    private final String emailAddress;

    public User(String username, String realName, String emailAddress) {
        this.username = username;
        this.realName = realName;
        this.emailAddress = emailAddress;
    }

    public String getUsername() { return username; }
    public String getRealName() { return realName; }
    public String getEmailAddress() { return emailAddress; }
}
