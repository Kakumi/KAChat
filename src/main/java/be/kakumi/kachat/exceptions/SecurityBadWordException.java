package be.kakumi.kachat.exceptions;

import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.utils.MessageManager;

public class SecurityBadWordException extends CheckerException {
    public SecurityBadWordException(String word) {
        super(KAChatAPI.getInstance().getMessageManager().get(MessageManager.SECURITY_BAD_WORD, word, null));
    }
}
