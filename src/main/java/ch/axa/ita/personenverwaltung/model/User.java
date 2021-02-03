package ch.axa.ita.personenverwaltung.model;

import static ch.axa.ita.personenverwaltung.utility.HashGenerator.hash;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String token;

    public User() {
        super();
    }

    public User(String name, String email, String password) {
        this();

        this.name = name;
        this.email = email;
        this.password = hash(password);
    }

    public User(String name, String email, String password, String token) {
        this(name, email, password);
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
