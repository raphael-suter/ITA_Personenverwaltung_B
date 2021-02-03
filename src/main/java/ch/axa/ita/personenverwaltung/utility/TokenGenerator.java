package ch.axa.ita.personenverwaltung.utility;

import ch.axa.ita.personenverwaltung.model.Token;

import java.util.UUID;

public class TokenGenerator {
    public static Token token() {
        String uuid = UUID
                .randomUUID()
                .toString();

        return new Token(uuid);
    }
}
