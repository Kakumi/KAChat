package be.kakumi.kachat.exceptions;

import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.utils.MessageManager;

public class SecurityProtectException extends CheckerException {
    public SecurityProtectException() {
        super(KAChatAPI.getInstance().getMessageManager().get(MessageManager.SECURITY_STRANGE));
    }
}
