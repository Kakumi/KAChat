package be.kakumi.kachat.api;

import be.kakumi.kachat.enums.PlayerChangeChannelReason;
import be.kakumi.kachat.events.PlayerUpdateChannelEvent;
import be.kakumi.kachat.exceptions.AddChannelException;
import be.kakumi.kachat.models.Channel;
import be.kakumi.kachat.models.LastMessage;
import be.kakumi.kachat.utils.Formatter;
import be.kakumi.kachat.utils.*;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class KAChatAPI implements Placeholder {
    private static KAChatAPI instance;

    private String defaultFormat; //Default chat format
    private Channel defaultChannel; //Default channel to use
    private List<Channel> channels; //List of registered channels (with /channel)
    private HashMap<UUID, Channel> playersChannel; //List of users channel
    private ChatManager chatManager; //Chat manager
    private ChatSaver chatSaver; //Chat saver
    private List<Checker> checkers; //Checkers list
    private List<Formatter> messageFormatters; //Message formatters list
    private List<Placeholder> placeholders; //Placeholders list
    private HashMap<UUID, LastMessage> lastMessages; //Last messages list
    private MessageManager messageManager; //All sentences from language file

    private KAChatAPI() {
        this.defaultFormat = "{player}§7: §f{message}";
        this.defaultChannel = null;
        this.channels = new ArrayList<>();
        this.playersChannel = new HashMap<>();
        this.chatSaver = null;
        this.chatManager = new ChatManager();
        this.checkers = new ArrayList<>();
        this.messageFormatters = new ArrayList<>();
        this.placeholders = new ArrayList<>();
        this.placeholders.add(this);
        this.lastMessages = new HashMap<>();
        this.messageManager = null;
    }

    /***
     * Get the API instance through a Singleton
     * @return KAChatAPI instance
     */
    public static KAChatAPI getInstance() {
        if (instance == null) {
            instance = new KAChatAPI();
        }

        return instance;
    }

    /***
     * Add a new channel in the channel list.
     * @param channel New channel you want to add
     * @throws AddChannelException Channel cannot be added
     */
    public void addChannel(@NotNull Channel channel) throws AddChannelException {
        if (channels.contains(channel) || getChannelFromCommand(channel.getCommand()) != null) {
            throw new AddChannelException();
        }

        channels.add(channel);
    }

    /***
     * Remove a channel from the channel list. If the channel doesn't exist, it is as if it has been removed.
     * @param channel Channel you want to remove.
     * @return List of player who lost their channel due to deletion
     */
    public List<UUID> removeChannel(@NotNull Channel channel) {
        List<UUID> removed = new ArrayList<>();
        for(Map.Entry<UUID, Channel> entry : playersChannel.entrySet()) {
            if (entry.getValue() == channel) {
                removed.add(entry.getKey());
                playersChannel.remove(entry.getKey());
            }
        }

        channels.remove(channel);
        return removed;
    }

    /***
     * Remove a channel from the channel list based on the command name.
     * You can remove a channel by command if you updated a channel and want to remove the last one.
     * @param command Command of a channel you want to remove
     * @return List of player who lost their channel due to deletion
     */
    public List<UUID> removeChannel(String command) {
        Channel channel = getChannelFromCommand(command);
        if (channel != null) {
            return removeChannel(channel);
        }

        return new ArrayList<>();
    }

    /***
     * Get list of registered channels.
     * @return Channel list
     */
    public List<Channel> getChannels() {
        return channels;
    }

    /***
     * Clear the channel list by removing those that are allowed to be deleted.
     */
    public void clearChannels() {
        List<Channel> channels = new ArrayList<>();
        for(Channel channel : this.channels) {
            if (!channel.isDelete()) channels.add(channel);
        }

        this.channels = channels;
    }
    
    /***
     * Get a channel from the channel list by the content of the message sent.
     * @param message Full text of the message
     * @return Channel with the override symbol matching the start of the message. Null if it doesn't exist.
     */
    @Nullable
    public Channel getChannelFromMessage(String message) {
        for(Channel channel : channels) {
        	String symbol = channel.getOverrideSymbol();

            if (StringUtils.isNotEmpty(symbol) && message.startsWith(symbol)) return channel;
        }

        return null;
    }

    /***
     * Get a channel from the channel list by a command name.
     * @param command Name of the command
     * @return Channel how has the command name. Null if it doesn't exist.
     */
    @Nullable
    public Channel getChannelFromCommand(String command) {
        for(Channel channel : channels) {
            if (channel.getCommand().equalsIgnoreCase(command)) return channel;
        }

        return null;
    }

    /***
     * Get a channel who used the world name as an auto world.
     * @param world Name of the world
     * @return Channel how has the world name as an auto world. Null if it doesn't exist.
     */
    public List<Channel> getChannelsFromWorld(String world) {
        List<Channel> channelsWorld = new ArrayList<>();
        for(Channel channel : channels) {
            if (channel.getSetAutoWorld().equalsIgnoreCase(world)) channelsWorld.add(channel);
        }

        return channelsWorld;
    }

    /***
     * Change default format in the chat.
     * @param defaultFormat Format in the chat like "{player}: {message}"
     */
    public void setDefaultFormat(@NotNull String defaultFormat) {
        this.defaultFormat = defaultFormat;
    }

    /***
     * Change default channel used by players.
     * @param defaultChannel Channel used by default by players
     */
    public void setDefaultChannel(@NotNull Channel defaultChannel) {
        this.defaultChannel = defaultChannel;
    }

    /***
     * Get the default format registered.
     * @return Format used by default
     */
    public String getDefaultFormat() {
        return defaultFormat;
    }

    /***
     * Get the default channel registered.
     * @return Channel used by default
     */
    public Channel getDefaultChannel() {
        return defaultChannel;
    }

    /***
     * Get the channel the player is using, default channel if player isn't using one.
     * @param player Player you want to get his channel
     * @return Channel used
     */
    public Channel getPlayerChannel(Player player) {
        Channel channel = playersChannel.get(player.getUniqueId());
        if (channel == null) return defaultChannel;

        return channel;
    }

    /***
     * Change the channel the player uses.
     * @param player Player you want to change his channel
     * @param channel Channel to use
     */
    public void setPlayerChannel(Player player, Channel channel) {
        setPlayerChannel(player, channel, PlayerChangeChannelReason.UNKNOWN);
    }

    /***
     * Change the channel the player uses.
     * @param player Player you want to change his channel
     * @param channel Channel to use
     * @param reason Reason of the change
     */
    public void setPlayerChannel(Player player, Channel channel, PlayerChangeChannelReason reason) {
        Bukkit.getPluginManager().callEvent(new PlayerUpdateChannelEvent(player, playersChannel.get(player.getUniqueId()), channel, reason));

        playersChannel.remove(player.getUniqueId());
        playersChannel.put(player.getUniqueId(), channel);
    }

    /***
     * Reset the player channel to default
     * @param player Player to reset
     * @param reason Reason of the change
     */
    public void removePlayerChannel(Player player, PlayerChangeChannelReason reason) {
        Bukkit.getPluginManager().callEvent(new PlayerUpdateChannelEvent(player, playersChannel.get(player.getUniqueId()), defaultChannel, reason));
        playersChannel.remove(player.getUniqueId());
    }

    /***
     * Reset the player channel to default
     * @param player Player to reset
     */
    public void removePlayerChannel(Player player) {
        removePlayerChannel(player, PlayerChangeChannelReason.UNKNOWN);
    }

    /***
     * Get list of player and the channel they used.
     * @return HashMap of UUID and channel
     */
    public HashMap<UUID, Channel> getPlayersChannel() {
        return playersChannel;
    }

    /***
     * Get the chat manager, it's used to get players, colors to use and send the message.
     * @return ChatManager
     */
    public ChatManager getChatManager() {
        return chatManager;
    }

    /***
     * Change the chat manager to use, you can use this method when you to use your default ChatManager.
     * @param chatManager Your ChatManager
     */
    public void setChatManager(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    /***
     * Get the chat saver, it's used to save messages into a file.
     * @return ChatManager
     */
    @Nullable
    public ChatSaver getChatSaver() {
        return chatSaver;
    }

    /***
     * Change the chat saver to use, you can use this method when you want to use your default ChatSaver.
     * @param chatSaver your ChatSaver
     */
    public void setChatSaver(ChatSaver chatSaver) {
        this.chatSaver = chatSaver;
    }

    /***
     * Get list of registered checkers.
     * @return Checker list
     */
    public List<Checker> getCheckers() {
        return checkers;
    }

    /***
     * Clear the checker list by removing those that are allowed to be deleted.
     */
    public void clearCheckers() {
        List<Checker> checkers = new ArrayList<>();
        for(Checker checker : this.checkers) {
            if (!checker.delete()) checkers.add(checker);
        }

        this.checkers = checkers;
    }

    /***
     * Get list of registered message formatters.
     * @return Message formatter list
     */
    public List<Formatter> getMessageFormatters() {
        return messageFormatters;
    }

    /***
     * Clear the message formatter list by removing those that are allowed to be deleted.
     */
    public void clearMessageFormatters() {
        List<Formatter> formatters = new ArrayList<>();
        for(Formatter formatter : this.messageFormatters) {
            if (!formatter.delete()) {
                formatters.add(formatter);
            }
        }

        this.messageFormatters = formatters;
    }

    /***
     * Get list of registered placeholders.
     * @return Placeholder list
     */
    public List<Placeholder> getPlaceholders() {
        return placeholders;
    }

    /***
     * Get the last message list.
     * @return HashMap of UUID and LastMessage
     */
    public HashMap<UUID, LastMessage> getLastMessages() {
        return lastMessages;
    }

    /***
     * Update the last message the player sent.
     * @param player Player who sent the message
     * @param message Message sent (not formatted)
     */
    public void updateLastMessage(Player player, String message) {
        if (lastMessages.containsKey(player.getUniqueId())) {
            lastMessages.get(player.getUniqueId()).update(message);
        } else {
            lastMessages.put(player.getUniqueId(), new LastMessage(message));
        }
    }

    public String format(@NotNull Player player, @NotNull String message) {
        Channel channel = getPlayerChannel(player);

        message = message.replaceAll(" {2}", " ");
        message = message.replace("{channel}", channel.getPrefix());
        message = message.replace("{color}", chatManager.getPlayerColor(player));
        message = message.replace("{chat_color}", chatManager.getChatColor(player, channel));
        message = message.replace("{player}", player.getName());
        message = message.replace("&", "§"); //Because with ColourFormatter, message will not be updated

        return message.trim();
    }

    /***
     * Get the message manager to get all messages for this plugin
     * @return MessageManager
     */
    public MessageManager getMessageManager() {
        return messageManager;
    }

    /***
     * Don't change this from your plugin
     * Set the message manager to get all messages for this plugin
     * @param messageManager MessageManager
     */
    public void setMessageManager(@NotNull MessageManager messageManager) {
        this.messageManager = messageManager;
    }
}
