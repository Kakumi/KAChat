package be.kakumi.kachat.utils;

import org.bukkit.entity.Player;

public interface Placeholder {
    /***
     * Replace placeholder inside the message and chat format.
     * @param player Player who sent the message
     * @param message Message to send (format + message)
     * @return Message to send with placeholder replace
     */
    String format(Player player, String message);
}
