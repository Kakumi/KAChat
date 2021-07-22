package be.kakumi.kachat.middlewares.message;

import be.kakumi.kachat.utils.Formatter;
import org.bukkit.entity.Player;

public class ColorFormatter implements Formatter {
    public String format(Player player, String message) {
        if (player.hasPermission("kachat.bypass.color")) {
            message = message.replace("&", "§");
        } else {
            message = message.replaceAll("§.", "");
            message = message.replaceAll("&.", "");
        }

        return message;
    }

    public boolean delete() {
        return true;
    }
}
