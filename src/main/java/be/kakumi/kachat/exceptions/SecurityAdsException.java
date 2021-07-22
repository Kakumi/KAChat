package be.kakumi.kachat.exceptions;

import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.utils.MessageManager;

public class SecurityAdsException extends CheckerException {
    public SecurityAdsException() {
        super(KAChatAPI.getInstance().getMessageManager().get(MessageManager.SECURITY_ADS));
    }
}
