package be.kakumi.kachat.exceptions;

import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.utils.MessageManager;

public class SecurityCapsLockException extends CheckerException {
    public SecurityCapsLockException() {
        super(KAChatAPI.getInstance().getMessageManager().get(MessageManager.SECURITY_CAPSLOCK));
    }
}
