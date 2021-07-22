package be.kakumi.kachat.exceptions;

public class UpdateChannelCommandException extends Exception {
    public UpdateChannelCommandException() {
        super("You can't change this command because it already exist for another channel.");
    }
}
