package be.kakumi.kachat.exceptions;

public class MessagesFileException extends Exception {
    public MessagesFileException() {
        super("Unable to create or read the messages file.");
    }
}
