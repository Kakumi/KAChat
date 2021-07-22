package be.kakumi.kachat.exceptions;

public class SecurityAdsException extends CheckerException {
    public SecurityAdsException() {
        super("Your message contains advertisement and it's not allowed!");
    }
}
