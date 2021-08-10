package be.kakumi.kachat.utils;

import be.kakumi.kachat.models.Channel;
import org.bukkit.entity.Player;

import java.util.List;

public interface ChatManager {
    /***
     * Use to send the message to a list of players.
     * @param message Message to send (already formated)
     * @param receivers List of players who can receive the message
     */
    void sendMessage(String message, List<Player> receivers);

    /***
     * This method get players who are available to receive the message
     * This method shouldn't be override, it's better to override getNearbyPlayers and filterReceivers
     * @param channel Channel used
     * @param player Player who sent the message
     * @return List of available players
     */
    List<Player> getValidReceivers(Channel channel, Player player);

    /***
     * Get the colour for the player's name in the chat
     * Override it to add custom behaviour to choose the color
     * @param player Player who sent the message
     * @return Player color in the chat (& or §)
     */
    String getPlayerColor(Player player);

    /***
     * Get the colour for the message in the chat
     * Override it to add custom behaviour to choose the color
     * @param player Player who sent the message
     * @param channel Channel used for the message
     * @return Player color in the chat (& or §)
     */
    String getChatColor(Player player, Channel channel);
}
