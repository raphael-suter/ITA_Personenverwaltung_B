package ch.axa.ita.personenverwaltung.utility;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ResponseGenerator {
    private static ResponseEntity response(HttpStatus httpStatus) {
        return ResponseEntity
                .status(httpStatus)
                .build();
    }

    private static ResponseEntity response(HttpStatus httpStatus, Object object) {
        return ResponseEntity
                .status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(object);
    }

    public static ResponseEntity notFound() {
        return response(HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity notFound(Object object) {
        return response(HttpStatus.NOT_FOUND, object);
    }

    public static ResponseEntity unauthorized() {
        return response(HttpStatus.UNAUTHORIZED);
    }

    public static ResponseEntity unauthorized(Object object) {
        return response(HttpStatus.UNAUTHORIZED, object);
    }

    public static ResponseEntity badRequest() {
        return response(HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity badRequest(Object object) {
        return response(HttpStatus.BAD_REQUEST, object);
    }

    public static ResponseEntity ok() {
        return response(HttpStatus.OK);
    }

    public static ResponseEntity ok(Object object) {
        return response(HttpStatus.OK, object);
    }
}
