package be.kakumi.kachat.utils;

import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.models.Channel;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SimpleChatManager implements ChatManager {
    /***
     * Use to send the message to a list of players.
     * @param message Message to send (already formated)
     * @param receivers List of players who can receive the message
     */
    public void sendMessage(String message, List<Player> receivers) {
        for(Player p : receivers) {
            p.sendMessage(message);
        }
    }

    /***
     * This method get players who are available to receive the message
     * This method shouldn't be override, it's better to override getNearbyPlayers and filterReceivers
     * @param channel Channel used
     * @param player Player who sent the message
     * @return List of available players
     */
    public List<Player> getValidReceivers(Channel channel, Player player) {
        List<Player> players = getNearbyPlayers(channel, player);

        return filterReceivers(players, channel);
    }

    /***
     * You can use this method to get all receivers that meets restrictions.
     * It filters players through world and range.
     * Override it to add custom behaviour
     * @param channel Channel used
     * @param player Player wo sent the message
     * @return List of player that meets the restrictions
     */
    protected List<Player> getNearbyPlayers(Channel channel, Player player) {
        List<Player> nearbyPlayers = new ArrayList<>();
        if (channel.getRange() == 0) {
            if (!channel.getWorld().equals("")) {
                World world = Bukkit.getWorld(channel.getWorld());
                if (world != null) {
                    nearbyPlayers.addAll(world.getPlayers());
                }
            } else {
                nearbyPlayers.addAll(Bukkit.getServer().getOnlinePlayers());
            }
        } else {
            for(Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                if (onlinePlayer.getWorld() == player.getWorld()) {
                    double onlinePlayerLocationX = Math.abs(onlinePlayer.getLocation().getX());
                    double onlinePlayerLocationZ = Math.abs(onlinePlayer.getLocation().getZ());
                    double playerLocationX = Math.abs(player.getLocation().getX());
                    double playerLocationZ = Math.abs(player.getLocation().getZ());
                    double differenceX = Math.abs(onlinePlayerLocationX - playerLocationX);
                    double differenceZ = Math.abs(onlinePlayerLocationZ - playerLocationZ);

                    if (differenceX < channel.getRange() && differenceZ < channel.getRange()) {
                        nearbyPlayers.add(onlinePlayer);
                    }
                }
            }
        }

        return nearbyPlayers;
    }

    /***
     * Use to filter players that meets restrictions to check if they are able to receive.
     * This method check if players can receive the message.
     * @param players List of players wo meets restrictions
     * @param channel Use to check some parameters like permissions
     * @return Filtered list of players that can receive the message
     */
    protected List<Player> filterReceivers(List<Player> players, Channel channel) {
        List<Player> receivers = new ArrayList<>();
        for(Player receiver : players) {
            if (channel.hasPermissionToSee(receiver)) {
                if (channel.isForInside()) {
                    if (KAChatAPI.getInstance().getPlayerChannel(receiver) == channel) {
                        receivers.add(receiver);
                    }
                } else {
                    receivers.add(receiver);
                }
            }
        }

        return receivers;
    }

    /***
     * Get the colour for the player's name in the chat
     * Override it to add custom behaviour to choose the color
     * @param player Player who sent the message
     * @return Player color in the chat (& or §)
     */
    public String getPlayerColor(Player player) {
        if (player.isOp()) return "&4";
        return "&f";
    }

    /***
     * Get the colour for the message in the chat
     * Override it to add custom behaviour to choose the color
     * @param player Player who sent the message
     * @param channel Channel used for the message
     * @return Player color in the chat (& or §)
     */
    public String getChatColor(Player player, Channel channel) {
        return channel.getColor();
    }
}
