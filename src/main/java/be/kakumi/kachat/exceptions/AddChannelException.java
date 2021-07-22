package be.kakumi.kachat.exceptions;

public class AddChannelException extends Exception {
    public AddChannelException() {
        super("There is an error with this channel, check if command is already registered or if channel already exist");
    }
}
