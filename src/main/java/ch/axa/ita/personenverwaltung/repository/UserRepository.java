package ch.axa.ita.personenverwaltung.repository;

import ch.axa.ita.personenverwaltung.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    private int id;
    private List<User> users;

    public UserRepository() {
        super();

        id = 0;
        users = new ArrayList<>();
    }

    public User create(User user) {
        user.setId(id);

        users.add(user);
        id++;

        return user;
    }

    public Optional<User> readById(int id) {
        return users
                .stream()
                .filter(u -> u.getId() == id)
                .findFirst();
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

    public boolean update(User user) {
        Optional<User> userFromDB = readById(user.getId());

        if (userFromDB.isPresent()) {
            int index = users.indexOf(userFromDB);
            users.set(index, user);

            return true;
        }

        return false;
    }

    public boolean delete(int id) {
        Optional<User> userFromDB = readById(id);

        if (userFromDB.isPresent()) {
            users.remove(userFromDB);
            return true;
        }

        return false;
    }
}
