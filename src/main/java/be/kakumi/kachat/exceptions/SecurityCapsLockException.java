package be.kakumi.kachat.exceptions;

public class SecurityCapsLockException extends CheckerException {
    public SecurityCapsLockException() {
        super("Your message contains too much characters in uppercase, please fix it.");
    }
}
