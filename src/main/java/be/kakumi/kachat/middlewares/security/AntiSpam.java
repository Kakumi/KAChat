package be.kakumi.kachat.middlewares.security;

import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.exceptions.CheckerException;
import be.kakumi.kachat.exceptions.SecuritySpamException;
import be.kakumi.kachat.models.LastMessage;
import be.kakumi.kachat.utils.Checker;
import org.bukkit.entity.Player;

public class AntiSpam implements Checker {
    private int max;
    public AntiSpam(int max) {
        this.max = max;
    }

    public boolean valid(Player player, String message) throws CheckerException {
        if (player.hasPermission("kachat.bypass.spam")) return true;

        LastMessage lastMessage = KAChatAPI.getInstance().getLastMessages().get(player.getUniqueId());
        if (lastMessage != null && lastMessage.isSameMessage(message, max)) {
            throw new SecuritySpamException();
        }

        return true;
    }

    public boolean delete() {
        return true;
    }
}
