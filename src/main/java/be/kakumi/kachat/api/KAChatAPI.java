package be.kakumi.kachat.api;

import be.kakumi.kachat.exceptions.AddChannelException;
import be.kakumi.kachat.models.Channel;
import be.kakumi.kachat.models.LastMessage;
import be.kakumi.kachat.utils.Formatter;
import be.kakumi.kachat.utils.*;
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
    private ChatManager chatManager; //
    private ChatSaver chatSaver;
    private List<Checker> checkers;
    private List<Formatter> messageFormatters;
    private List<Placeholder> placeholders;
    private HashMap<UUID, LastMessage> lastMessages;

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
    }

    public static KAChatAPI getInstance() {
        if (instance == null) {
            instance = new KAChatAPI();
        }

        return instance;
    }

    public void addChannel(@NotNull Channel channel) throws AddChannelException {
        if (channels.contains(channel) || getChannelFromCommand(channel.getCommand()) != null) {
            throw new AddChannelException();
        }

        channels.add(channel);
    }

    public void removeChannel(@NotNull Channel channel) {
        for(Map.Entry<UUID, Channel> entry : playersChannel.entrySet()) {
            if (entry.getValue() == channel) {
                Player player = Bukkit.getServer().getPlayer(entry.getKey());
                if (player != null) {
                    player.sendMessage("§cYou have been exclude from your channel because it no longer exist.");
                }
                playersChannel.remove(entry.getKey());
            }
        }

        channels.remove(channel);
    }

    public void removeChannel(String command) {
        Channel channel = getChannelFromCommand(command);
        if (channel != null) {
            removeChannel(channel);
        }
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void clearChannels() {
        List<Channel> channels = new ArrayList<>();
        for(Channel channel : this.channels) {
            if (!channel.isDelete()) channels.add(channel);
        }

        this.channels = channels;
    }

    @Nullable
    public Channel getChannelFromCommand(String command) {
        for(Channel channel : channels) {
            if (channel.getCommand().equalsIgnoreCase(command)) return channel;
        }

        return null;
    }

    public List<Channel> getChannelsFromWorld(String world) {
        List<Channel> channelsWorld = new ArrayList<>();
        for(Channel channel : channels) {
            if (channel.getSetAutoWorld().equalsIgnoreCase(world)) channelsWorld.add(channel);
        }

        return channelsWorld;
    }

    public void setDefaultFormat(@NotNull String defaultFormat) {
        this.defaultFormat = defaultFormat;
    }

    public void setDefaultChannel(@NotNull Channel defaultChannel) {
        this.defaultChannel = defaultChannel;
    }

    public String getDefaultFormat() {
        return defaultFormat;
    }

    public Channel getDefaultChannel() {
        return defaultChannel;
    }

    public Channel getPlayerChannel(Player player) {
        Channel channel = playersChannel.get(player.getUniqueId());
        if (channel == null) return defaultChannel;

        return channel;
    }

    public void setPlayerChannel(Player player, Channel channel) {
        playersChannel.remove(player.getUniqueId());
        playersChannel.put(player.getUniqueId(), channel);
    }

    public HashMap<UUID, Channel> getPlayersChannel() {
        return playersChannel;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public void setChatManager(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @Nullable
    public ChatSaver getChatSaver() {
        return chatSaver;
    }

    public void setChatSaver(ChatSaver chatSaver) {
        this.chatSaver = chatSaver;
    }

    public List<Checker> getCheckers() {
        return checkers;
    }

    public void clearCheckers() {
        List<Checker> checkers = new ArrayList<>();
        for(Checker checker : this.checkers) {
            if (!checker.delete()) checkers.add(checker);
        }

        this.checkers = checkers;
    }

    public List<Formatter> getMessageFormatters() {
        return messageFormatters;
    }

    public void clearMessageFormatters() {
        List<Formatter> formatters = new ArrayList<>();
        for(Formatter formatter : this.messageFormatters) {
            if (!formatter.delete()) {
                formatters.add(formatter);
            }
        }

        this.messageFormatters = formatters;
    }

    public List<Placeholder> getPlaceholders() {
        return placeholders;
    }

    public HashMap<UUID, LastMessage> getLastMessages() {
        return lastMessages;
    }

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
        message = message.replace("{channel}", channel.getPrefix());
        message = message.replace("{player}", player.getName());
        message = message.replace("&", "§"); //Because with ColourFormatter, message will not be updated

        return message.trim();
    }
}
