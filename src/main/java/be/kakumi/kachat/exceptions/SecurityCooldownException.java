package be.kakumi.kachat.exceptions;

import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.utils.MessageManager;

import java.util.Collections;

public class SecurityCooldownException extends CheckerException {
    public SecurityCooldownException(double seconds) {
        super(KAChatAPI.getInstance().getMessageManager().get(MessageManager.SECURITY_COOLDOWN, Collections.singletonList(String.format("%.1f", seconds))));
    }
}
