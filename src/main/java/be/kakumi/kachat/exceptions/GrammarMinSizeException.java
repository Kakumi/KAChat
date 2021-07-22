package be.kakumi.kachat.exceptions;

public class GrammarMinSizeException extends CheckerException {
    public GrammarMinSizeException() {
        super("Your message is too short.");
    }
}
