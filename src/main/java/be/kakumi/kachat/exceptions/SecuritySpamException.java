package be.kakumi.kachat.exceptions;

import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.utils.MessageManager;

public class SecuritySpamException extends CheckerException {
    public SecuritySpamException() {
        super(KAChatAPI.getInstance().getMessageManager().get(MessageManager.SECURITY_SPAM));
    }
}
