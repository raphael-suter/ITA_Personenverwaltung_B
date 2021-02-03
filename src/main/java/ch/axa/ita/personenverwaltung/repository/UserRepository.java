package ch.axa.ita.personenverwaltung.repository;

import ch.axa.ita.personenverwaltung.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    private int id;
    private final List<User> users = new ArrayList<>();

    public UserRepository() {
        super();
        id = 0;
    }

    public User create(User user) {
        user.setId(id);

        users.add(user);
        id++;

        return user;
    }

    public Optional<User> readByEmail(String email) {
        return users
                .stream()
                .filter(u -> u
                        .getEmail()
                        .equals(email))
                .findFirst();
    }

    public Optional<User> readByToken(String token) {
        return users
                .stream()
                .filter(u ->
                        u.getToken() != null && u.getToken()
                                .equals(token))
                .findFirst();
    }
}
