package be.kakumi.kachat.exceptions;

public class SecuritySpamException extends CheckerException {
    public SecuritySpamException() {
        super("Your message is the same as before, you can't send it again.");
    }
}
