package ch.axa.ita.personenverwaltung.repository;

import ch.axa.ita.personenverwaltung.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VerificationRepository {
    private final List<User> users;

    public VerificationRepository() {
        super();
        users = new ArrayList<>();
    }

    public User create(User user) {
        users.add(user);
        return user;
    }

    public Optional<User> readByToken(String token) {
        return users
                .stream()
                .filter(u -> u
                        .getToken()
                        .equals(token))
                .findFirst();
    }

    public void delete(User user) {
        users.remove(user);
    }
}
