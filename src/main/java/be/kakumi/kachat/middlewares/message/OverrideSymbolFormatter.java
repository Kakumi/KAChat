package be.kakumi.kachat.middlewares.message;

import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.models.Channel;
import be.kakumi.kachat.utils.Formatter;
import org.bukkit.entity.Player;

public class OverrideSymbolFormatter implements Formatter {
    public String format(Player player, String message) {
        Channel channel = KAChatAPI.getInstance().getChannelFromMessage(message);

        if (channel != null && message.startsWith(channel.getOverrideSymbol())) {
            message = message.substring(channel.getOverrideSymbol().length());
        }

        return message;
    }

    public boolean delete() {
        return true;
    }
}
