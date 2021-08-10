package be.kakumi.kachat.exceptions;

import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.utils.MessageManager;

import java.util.Collections;

public class SecurityBadWordException extends CheckerException {
    public SecurityBadWordException(String word) {
        super(KAChatAPI.getInstance().getMessageManager().get(MessageManager.SECURITY_BAD_WORD, Collections.singletonList(word)));
    }
}
