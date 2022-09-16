package be.kakumi.kachat.utils;

import be.kakumi.kachat.KAChat;
import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.events.ChannelReceiveMessageEvent;
import be.kakumi.kachat.exceptions.CheckerException;
import be.kakumi.kachat.models.Channel;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatManager {
    /**
     * Send a message for a player
     * @param player
     * @param message
     * @param channel
     */
    public boolean sendMessage(Player player, String message, Channel channel) {
        try {
            //Format the message only
            for(Formatter formatter : KAChatAPI.getInstance().getMessageFormatters()) {
                message = formatter.format(player, message);
            }
            //Check if message is valid
            boolean toSend = checkMessage(player, channel, message);

            if (toSend) {
                //Here to be sure we have the format and the message, even if KAChatAPI Formatter is not loaded
                String messageFormat = KAChatAPI.getInstance().getChatManager().getChatFormat(channel, player).replace("{message}", message);
                //Replace placeholder for the whole message and format
                for(Placeholder placeholder : KAChatAPI.getInstance().getPlaceholders()) {
                    messageFormat = placeholder.format(player, channel, messageFormat);
                }
                //Process all color codes from the original message and the placeholders
                messageFormat = KAChatAPI.getInstance().processColors(messageFormat);
                messageFormat = KAChatAPI.getInstance().processBeautifier(messageFormat);

                List<Player> receivers = getValidReceivers(channel, player);

                if (KAChatAPI.getInstance().getPlayerTextHover().getLines().size() > 0) {
                    //Check and potentially add hover message
                    String targetHover = null;
                    if (messageFormat.contains(player.getName())) {
                        targetHover = player.getName();
                    } else if (messageFormat.contains(player.getDisplayName())) {
                        targetHover = player.getDisplayName();
                    }

                    if (targetHover != null) {
                        BaseComponent[] component = generateTextWithHover(messageFormat, player, channel, targetHover);

                        sendMessage(component, receivers);
                    } else {
                        sendMessage(messageFormat, receivers);
                    }
                } else {
                    sendMessage(messageFormat, receivers);
                }

                final String messageFormatFinal = messageFormat;
                final String messageFinal = message;
                //Bukkit.getPluginManager().callEvent(new ChannelReceiveMessageEvent(channel, player, receivers, messageFormatFinal, messageFinal, toSend));
                //Because we can't run event from an asynchronous thread
                Bukkit.getScheduler().runTaskLater(KAChat.getInstance(), () -> Bukkit.getPluginManager().callEvent(new ChannelReceiveMessageEvent(channel, player, receivers, messageFormatFinal, messageFinal, toSend)), 1);
            }

            return toSend;
        } catch (CheckerException e) {
            player.sendMessage(e.getMessage());
        } catch (Exception e2) {
            e2.printStackTrace();
        }

        return false;
    }

    private BaseComponent[] generateTextWithHover(String messageFormat, Player player, Channel channel, String targetHover) {
        int index = messageFormat.indexOf(targetHover);
        String before = messageFormat.substring(0, index);
        String after = messageFormat.substring(index + targetHover.length());
        TextComponent textComponent = new TextComponent(targetHover);
        String command = KAChatAPI.getInstance().getPlayerTextHover().getCommand();
        String hover = String.join("\n", KAChatAPI.getInstance().getPlayerTextHover().getLines());
        for(Placeholder placeholder : KAChatAPI.getInstance().getPlaceholders()) {
            command = placeholder.format(player, channel, command);
            hover = placeholder.format(player, channel, hover);
        }

        //Process all color codes from the original message and the placeholders
        hover = KAChatAPI.getInstance().processColors(hover);
        hover = KAChatAPI.getInstance().processBeautifier(hover);

        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + command));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hover)));

        ComponentBuilder mainComponent = new ComponentBuilder(before);
        mainComponent.append(textComponent);
        mainComponent.append(after);

        return mainComponent.create();
    }

    /**
     * Check if a message is valid using cherckers
     * @param player
     * @param message
     * @return true if message is valid
     * @throws CheckerException
     */
    private boolean checkMessage(Player player, Channel channel, String message) throws CheckerException {
        boolean toSend = true;

        for(Checker checker : KAChatAPI.getInstance().getCheckers()) {
            if (toSend && !checker.valid(player, channel, message)) {
                toSend = false;
            }
        }

        return toSend;
    }

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
     * Use to send the message to a list of players.
     * @param component Message to send (already formated) as a component
     * @param receivers List of players who can receive the message
     */
    public void sendMessage(BaseComponent component, List<Player> receivers) {
        for(Player p : receivers) {
            p.spigot().sendMessage(component);
        }
    }

    /***
     * Use to send the message to a list of players.
     * @param components Message to send (already formated) as a component
     * @param receivers List of players who can receive the message
     */
    public void sendMessage(BaseComponent[] components, List<Player> receivers) {
        for(Player p : receivers) {
            p.spigot().sendMessage(components);
        }
    }

    /***
     * This method get players who are available to receive the message
     * This method shouldn't be override, it's better to override getNearbyPlayers and filterReceivers
     * @param channel Channel used
     * @param player Player who sent the message
     * @return List of available players
     */
    public final List<Player> getValidReceivers(Channel channel, Player player) {
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
        String patternPermission = "kachat.playercolor.";
        PermissionAttachmentInfo permissionAttachmentInfo = player.getEffectivePermissions().stream().filter(x -> x.getPermission().contains(patternPermission)).findFirst().orElse(null);
        if (permissionAttachmentInfo != null) {
            if (permissionAttachmentInfo.getPermission().contains(patternPermission)) {
                return permissionAttachmentInfo.getPermission().replace(patternPermission, "");
            }
        }

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
        String patternPermission = "kachat.chatcolor.";
        List<PermissionAttachmentInfo> permissionAttachmentInfo = player.getEffectivePermissions().stream().filter(x -> x.getPermission().contains(patternPermission)).collect(Collectors.toList());
        if (permissionAttachmentInfo.size() != 0) {
            String patternPermissionChannel = patternPermission + channel.getCommand() + ".";
            PermissionAttachmentInfo channelColor = permissionAttachmentInfo.stream().filter(x -> x.getPermission().contains(patternPermissionChannel)).findFirst().orElse(null);
            if (channelColor == null) {
                PermissionAttachmentInfo chatColor = permissionAttachmentInfo.stream().filter(x -> x.getPermission().matches("^kachat\\.chatcolor\\.[^.]+$")).findFirst().orElse(null);
                if (chatColor != null) {
                    return chatColor.getPermission().replace(patternPermission, "");
                }
            } else {
                return channelColor.getPermission().replace(patternPermissionChannel, "");
            }
        }

        return channel.getColor();
    }

    /**
     * Get the chat format for the specific channel
     * @return Chat format
     */
    public String getChatFormat(Channel channel, Player player) {
        return channel.getFormat();
    }
}
