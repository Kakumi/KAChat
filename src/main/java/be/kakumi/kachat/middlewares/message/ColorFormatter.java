package be.kakumi.kachat.middlewares.message;

import be.kakumi.kachat.utils.Formatter;
import org.bukkit.entity.Player;

public class ColorFormatter implements Formatter {
    public String format(Player player, String message) {
        if (!player.hasPermission("kachat.bypass.color")) {
            message = message.replaceAll("§.", "");
            message = message.replaceAll("&.", "");
            message = message.replaceAll("&#[a-fA-F\\d]{6}", "");
        }

        return message;
    }

    public boolean delete() {
        return true;
    }
}
