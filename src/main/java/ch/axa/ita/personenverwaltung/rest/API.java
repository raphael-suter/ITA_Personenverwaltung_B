package ch.axa.ita.personenverwaltung.rest;

import ch.axa.ita.personenverwaltung.model.*;
import ch.axa.ita.personenverwaltung.repository.UserRepository;
import ch.axa.ita.personenverwaltung.repository.VerificationRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static ch.axa.ita.personenverwaltung.utility.HashGenerator.hash;
import static ch.axa.ita.personenverwaltung.utility.Mail.send;
import static ch.axa.ita.personenverwaltung.utility.ResponseGenerator.*;
import static ch.axa.ita.personenverwaltung.utility.TokenGenerator.token;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class API {
    private final VerificationRepository verificationRepository = new VerificationRepository();
    private final UserRepository userRepository = new UserRepository();

    @PostMapping("/sign_up")
    private ResponseEntity signUp(@RequestBody SignUpData signUpData) {
        String name = signUpData.getName();
        String email = signUpData.getEmail();
        String password = signUpData.getPassword();
        String repPassword = signUpData.getRepPassword();

        if (isEmpty(name)) {
            return badRequest(new Message("Name darf nicht leer sein."));
        }

        if (isEmpty(email)) {
            return badRequest(new Message("E-Mail-Adresse darf nicht leer sein."));
        }

        if (userRepository.readByEmail(email).isPresent()) {
            return badRequest(new Message("Diese E-Mail-Adresse wird bereits verwendet."));
        }

        if (isEmpty(password)) {
            return badRequest(new Message("Passwort darf nicht leer sein."));
        }

        if (!password.equals(repPassword)) {
            return badRequest(new Message("Passwörter stimmen nicht überein."));
        }

        String token = token().getToken();
        User user = new User(name, email, password, token);

        send(email, "Verification", "http://localhost:3000/verify/" + token);
        verificationRepository.create(user);

        return ok();
    }

    @PostMapping("/verify")
    private ResponseEntity verify(@RequestBody Token token) {
        Optional<User> userFromDB = verificationRepository.readByToken(token.getToken());

        if (userFromDB.isPresent()) {
            User user = userFromDB.get();

            userRepository.create(user);
            verificationRepository.delete(user);

            return ok(new Message("Dein Account wurde erfolgreich verifiziert."));
        }

        return notFound(new Message("Dieser Link ist ungültig."));
    }

    @PostMapping("/sign_in")
    private ResponseEntity signIn(@RequestBody SignInData signInData) {
        Optional<User> userFromDB = userRepository.readByEmail(signInData.getEmail());

        if (userFromDB.isPresent()) {
            User user = userFromDB.get();

            String passwordFromDB = user.getPassword();
            String passwordFromSignInData = hash(signInData.getPassword());

            if (passwordFromDB.equals(passwordFromSignInData)) {
                Token token = token();
                user.setToken(token.getToken());

                return ok(token);
            }

            return badRequest(new Message("Falsches Passwort."));
        }

        return notFound(new Message("Dieser Benutzer wurde nicht gefunden."));
    }

    @GetMapping("/profile")
    private ResponseEntity loadProfile(@RequestHeader HttpHeaders httpHeaders) {
        Optional<User> userFromDB = getUserByToken(getTokenFromHttpHeaders(httpHeaders));

        if (userFromDB.isPresent()) {
            return ok(userFromDB.get());
        }

        return notFound(new Message("Dieser Benutzer wurde nicht gefunden."));
    }

    @GetMapping("/secured/data")
    private ResponseEntity loadSecuredData(@RequestHeader HttpHeaders httpHeaders) {
        return ok(new Message("Du kek."));
    }

    @PostMapping("/sign_out")
    private ResponseEntity signOut(@RequestHeader HttpHeaders httpHeaders) {
        Optional<User> userFromDB = getUserByToken(getTokenFromHttpHeaders(httpHeaders));

        if (userFromDB.isPresent()) {
            User user = userFromDB.get();
            user.setToken(null);

            return ok();
        }

        return notFound(new Message("Dieser Benutzer wurde nicht gefunden."));
    }

    private String getTokenFromHttpHeaders(HttpHeaders httpHeaders) {
        return httpHeaders
                .get("authorization")
                .get(0)
                .substring(7);
    }

    public Optional<User> getUserByToken(String token) {
        return userRepository.readByToken(token);
    }

    private boolean isEmpty(String input) {
        return input == null || input.isEmpty();
    }
}
