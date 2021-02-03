package ch.axa.ita.personenverwaltung.model;

public class Message {
    private String message;

    public Message() {
        super();
    }

    public Message(String error) {
        this.message = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
