package be.kakumi.kachat.api;

import be.kakumi.kachat.models.Channel;
import be.kakumi.kachat.utils.Placeholder;
import org.bukkit.entity.Player;

public class PlaceholderAPI implements Placeholder {
    public String format(Player player, Channel channel, String message) {
        return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, message);
    }
}
