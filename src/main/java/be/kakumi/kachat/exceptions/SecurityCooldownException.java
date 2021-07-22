package be.kakumi.kachat.exceptions;

public class SecurityCooldownException extends CheckerException {
    public SecurityCooldownException(double seconds) {
        super("You can't send your message, you still have to wait " + String.format("%.1f", seconds) + " seconds.");
    }
}
