package be.kakumi.kachat.exceptions;

public class SecurityBadWordException extends CheckerException {
    public SecurityBadWordException(String word) {
        super("Your message contains words who are not allowed : §4" + word + "§c.");
    }
}
