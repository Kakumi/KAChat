package be.kakumi.kachat.exceptions;

public class SecurityProtectException extends CheckerException {
    public SecurityProtectException() {
        super("Your message is strange, you can't send it.");
    }
}
