package be.kakumi.kachat.exceptions;

import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.utils.MessageManager;

public class SecurityMinSizeException extends CheckerException {
    public SecurityMinSizeException() {
        super(KAChatAPI.getInstance().getMessageManager().get(MessageManager.SECURITY_MIN_SIZE));
    }
}
