package be.kakumi.kachat.middlewares.security;

import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.exceptions.CheckerException;
import be.kakumi.kachat.exceptions.SecurityCooldownException;
import be.kakumi.kachat.models.Channel;
import be.kakumi.kachat.models.LastMessage;
import be.kakumi.kachat.utils.Checker;
import org.bukkit.entity.Player;

public class CooldownMessage implements Checker {
    public boolean valid(Player player, String message) throws CheckerException {
        if (player.hasPermission("kachat.bypass.cooldown")) return true;

        Channel channel = KAChatAPI.getInstance().getPlayerChannel(player);
        LastMessage lastMessage = KAChatAPI.getInstance().getLastMessages().get(player.getUniqueId());
        if (channel.getCooldown() == 0 || lastMessage == null || lastMessage.canSend(channel.getCooldown())) {
            return true;
        }

        double seconds = lastMessage.getSecondsRemaining(channel.getCooldown());
        throw new SecurityCooldownException(seconds);
    }

    public boolean delete() {
        return true;
    }
}
