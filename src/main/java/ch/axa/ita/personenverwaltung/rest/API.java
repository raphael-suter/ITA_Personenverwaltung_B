package ch.axa.ita.personenverwaltung.rest;

import ch.axa.ita.personenverwaltung.model.*;
import ch.axa.ita.personenverwaltung.repository.UserRepository;
import ch.axa.ita.personenverwaltung.repository.VerificationRepository;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

import static ch.axa.ita.personenverwaltung.utility.HashGenerator.hash;
import static ch.axa.ita.personenverwaltung.utility.Mail.send;
import static ch.axa.ita.personenverwaltung.utility.ResponseGenerator.*;
import static ch.axa.ita.personenverwaltung.utility.TokenGenerator.token;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class API {
    private static final String NAME_EMPTY = "Name darf nicht leer sein.";
    private static final String EMAIL_EMPTY = "E-Mail-Adresse darf nicht leer sein.";
    private static final String EMAIL_IN_USE = "Diese E-Mail-Adresse wird bereits verwendet.";
    private static final String PASSWORD_EMPTY = "Passwort darf nicht leer sein.";
    private static final String PASSWORD_NO_MATCH = "Passwörter stimmen nicht überein.";
    private static final String SUBJECT = "Verification";
    private static final String VERIFICATION_LINK = "http://localhost:3000/verify/";
    private static final String VERIFICATION_SUCCESSFUL = "Dein Account wurde erfolgreich verifiziert.";
    private static final String LINK_INVALID = "Dieser Link ist ungültig.";
    private static final String WRONG_PASSWORD = "Falsches Passwort.";
    private static final String USER_NOT_FOUND = "Dieser Benutzer wurde nicht gefunden.";
    private static final String SECURED_MESSAGE = "Du kek.";
    private static final String AUTHORIZATION_HEADER = "authorization";
    private static final int TOKEN_INDEX = 0;
    private static final int MIN_LENGTH = 7;
    private static final String URL_PATTERN = "/api/secured/*";
    private static final int LEVEL_ONE = 1;
    private static final int LEVEL_TWO = 2;

    private final VerificationRepository verificationRepository = new VerificationRepository();
    private final UserRepository userRepository = new UserRepository();

    @PostMapping("/sign_up")
    private ResponseEntity<?> signUp(@RequestBody SignUpData signUpData) {
        String name = signUpData.getName();
        String email = signUpData.getEmail();
        String password = signUpData.getPassword();
        String repPassword = signUpData.getRepPassword();

        if (isEmpty(name)) {
            return badRequest(new Message(NAME_EMPTY));
        }

        if (isEmpty(email)) {
            return badRequest(new Message(EMAIL_EMPTY));
        }

        if (userRepository.readByEmail(email).isPresent()) {
            return badRequest(new Message(EMAIL_IN_USE));
        }

        if (isEmpty(password)) {
            return badRequest(new Message(PASSWORD_EMPTY));
        }

        if (!password.equals(repPassword)) {
            return badRequest(new Message(PASSWORD_NO_MATCH));
        }

        String token = token().getToken();
        User user = new User(name, email, password, token);

        send(email, SUBJECT, VERIFICATION_LINK + token);
        verificationRepository.create(user);

        return ok();
    }

    @PostMapping("/verify")
    private ResponseEntity<?> verify(@RequestBody Token token) {
        Optional<User> userFromDB = verificationRepository.readByToken(token.getToken());

        if (userFromDB.isPresent()) {
            User user = userFromDB.get();

            userRepository.create(user);
            verificationRepository.delete(user);

            return ok(new Message(VERIFICATION_SUCCESSFUL));
        }

        return notFound(new Message(LINK_INVALID));
    }

    @PostMapping("/sign_in")
    private ResponseEntity<?> signIn(@RequestBody SignInData signInData) {
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

            return badRequest(new Message(WRONG_PASSWORD));
        }

        return notFound(new Message(USER_NOT_FOUND));
    }

    @GetMapping("/profile")
    private ResponseEntity<?> loadProfile(@RequestHeader HttpHeaders httpHeaders) {
        Optional<User> userFromDB = getUserByToken(getTokenFromHttpHeaders(httpHeaders));

        if (userFromDB.isPresent()) {
            return ok(userFromDB.get());
        }

        return notFound(new Message(USER_NOT_FOUND));
    }

    @GetMapping("/secured/message")
    private ResponseEntity<?> loadSecuredMessage() {
        return ok(new Message(SECURED_MESSAGE));
    }

    @PostMapping("/sign_out")
    private ResponseEntity<?> signOut(@RequestHeader HttpHeaders httpHeaders) {
        Optional<User> userFromDB = getUserByToken(getTokenFromHttpHeaders(httpHeaders));

        if (userFromDB.isPresent()) {
            User user = userFromDB.get();
            user.setToken(null);

            return ok();
        }

        return notFound(new Message(USER_NOT_FOUND));
    }

    private String getTokenFromHttpHeaders(HttpHeaders httpHeaders) {
        return Objects.requireNonNull(httpHeaders
                .get(AUTHORIZATION_HEADER))
                .get(TOKEN_INDEX)
                .substring(MIN_LENGTH);
    }

    public Optional<User> getUserByToken(String token) {
        return userRepository.readByToken(token);
    }

    private boolean isEmpty(String input) {
        return input == null || input.isEmpty();
    }

    @Bean
    public FilterRegistrationBean<LogFilter> registerLogFilter() {
        FilterRegistrationBean<LogFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new LogFilter());
        registrationBean.addUrlPatterns(URL_PATTERN);
        registrationBean.setOrder(LEVEL_TWO);

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<AuthenticationFilter> registerAuthenticationFilter() {
        FilterRegistrationBean<AuthenticationFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new AuthenticationFilter(this));
        registrationBean.addUrlPatterns(URL_PATTERN);
        registrationBean.setOrder(LEVEL_ONE);

        return registrationBean;
    }
}
