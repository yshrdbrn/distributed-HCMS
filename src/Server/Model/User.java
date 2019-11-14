package Server.Model;

public class User {
    private String id;

    public User() {
    }

    public User(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getLocalID() {
        return id.substring(4);
    }

    public String getCity() {
        return id.substring(0, 3);
    }

    public boolean isAdmin() {
        return id.charAt(3) == 'A';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id.equals(user.id);
    }
}
