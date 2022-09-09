package be.kakumi.kachat.models;

import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.exceptions.UpdateChannelCommandException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

@SuppressWarnings("rawtypes")
public class Channel {
    private String command;
    private String prefix;
    private String color;
    private int cooldown;
    private boolean listed;
    private Enum channelType;
    private int range;
    private String world;
    private String permissionToUse;
    private String permissionToSee;
    private boolean forInside;
    private String format;
    private String setAutoWorld;
    private boolean delete;
    private String overrideSymbol;
    private HashMap<String, Object> custom;

    public Channel(String prefix, String command, String format) {
        this.prefix = prefix;
        this.command = command;
        this.format = format;
        this.listed = true;
        this.color = "§f";
        this.channelType = null;
        this.range = 0;
        this.world = "";
        this.permissionToUse = "";
        this.permissionToSee = "";
        this.forInside = false;
        this.setAutoWorld = "";
        this.delete = true;
        this.overrideSymbol = "";
        this.custom = new HashMap<>();
    }

    public Channel(@NotNull String prefix, @NotNull String command) {
        this(prefix, command, "");
    }

    /***
     * Get the command, can be used as an identifier and also for the /channel command.
     * @return Command of the channel
     */
    public String getCommand() {
        return command;
    }

    /***
     * Set the command, can be used as an identifier and also for the /channel command.
     * @param command Command for the channel
     */
    public void setCommand(@NotNull String command) throws UpdateChannelCommandException {
        if (KAChatAPI.getInstance().getChannelFromCommand(command) == null) {
            throw new UpdateChannelCommandException();
        }

        this.command = command;
    }

    /***
     * Get the prefix to use in the chat for this channel.
     * @return Prefix of the channel
     */
    public String getPrefix() {
        return prefix;
    }

    /***
     * Set the prefix to use in the chat for this channel.
     * @param prefix Prefix for the channel
     */
    public void setPrefix(@NotNull String prefix) {
        this.prefix = prefix;
    }

    /***
     * Get the color to use in the chat for this channel.
     * @return Color of the channel
     */
    public String getColor() {
        return color;
    }

    /***
     * Set the color to use in the chat for this channel.
     * @param color Color for the channel
     */
    public void setColor(@NotNull String color) {
        this.color = color;
    }

    /***
     * Get the cooldown to use in the chat for this channel.
     * @return Cooldown of the channel
     */
    public int getCooldown() {
        return cooldown;
    }

    /***
     * Set the cooldown to use in the chat for this channel.
     * @param cooldown Cooldown for the channel
     */
    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    /***
     * Know if this channel can be listed through the command /channel.
     * @return If command can be listed
     */
    public boolean isListed() {
        return listed;
    }

    /***
     * Set if this channel can be listed through the command /channel.
     * @param listed True, this channel can be listed
     */
    public void setListed(boolean listed) {
        this.listed = listed;
    }

    /***
     * Get the type of this channel.
     * By default, no one is registered by this plugin but you can set your enum to create custom behaviour by
     * overriding ChatManager.
     * e.g. If your channel is used by a guild, get players by looping through the guild instead of the default system.
     * @return Type of this channel
     */
    public Enum getChannelType() {
        return channelType;
    }

    /***
     * Set the type of this channel.
     * By default, no one is registered by this plugin but you can set your enum to create custom behaviour by
     * overriding ChatManager.
     * e.g. If your channel is used by a guild, get players by looping through the guild instead of the default system.
     * @param channelType Any enumeration (channel type)
     */
    public void setChannelType(Enum channelType) {
        this.channelType = channelType;
    }

    /***
     * Get the range to select players available to receive the message.
     * @return Range to get the message
     */
    public int getRange() {
        return range;
    }

    /***
     * Set the range to select players available to receive the message.
     * @param range Range to get the message
     */
    public void setRange(int range) {
        this.range = range;
    }

    /***
     * Get the restricted world name for this channel. It will force player to leave this channel if the world they are not is
     * different than this one.
     * @return Name of the world
     */
    public String getWorld() {
        return world;
    }

    /***
     * Set the restricted world name for this channel. It will force player to leave this channel if the world they are not is
     * different than this one.
     * @param world Name of the world
     */
    public void setWorld(@NotNull String world) {
        this.world = world;
    }

    /***
     * Get the permission to use to send messages into this channel.
     * @return Permission to use
     */
    public String getPermissionToUse() {
        return permissionToUse;
    }

    /***
     * Set the permission to use to send messages into this channel.
     * @param permissionToUse Permission to use
     */
    public void setPermissionToUse(@NotNull String permissionToUse) {
        this.permissionToUse = permissionToUse;
    }

    /***
     * Check if player has the permission to use. It's better to use this one instead of getPermissionToUse
     * because this method check if permission is not empty and if player has the permission.
     * @param player Player you want to check
     * @return True if player has the permission to use
     */
    public boolean hasPermissionToUse(Player player) {
        return this.permissionToUse.equals("") || player.hasPermission(this.permissionToUse);
    }

    /***
     * Get the permission to see messages from this channel.
     * @return Permission to see
     */
    public String getPermissionToSee() {
        return permissionToSee;
    }

    /***
     * set the permission to see messages from this channel.
     * @param permissionToSee Permission to see
     */
    public void setPermissionToSee(@NotNull String permissionToSee) {
        this.permissionToSee = permissionToSee;
    }

    /***
     * Check if player has the permission to see. It's better to use this one instead of getPermissionToSee
     * because this method check if permission is not empty and if player has the permission.
     * @param player Player you want to check
     * @return True if player has the permission to see
     */
    public boolean hasPermissionToSee(Player player) {
        return this.permissionToSee.equals("") || player.hasPermission(this.permissionToSee);
    }

    /***
     * Know if players can receive the message only when they are inside this channel.
     * Can be useful for guild system.
     * @return If players must be inside this channel
     */
    public boolean isForInside() {
        return forInside;
    }

    /***
     * Set if players can receive the message only when they are inside this channel.
     * Can be useful for guild system.
     * @param forInside True, players must be inside
     */
    public void setForInside(boolean forInside) {
        this.forInside = forInside;
    }

    /***
     * Get the format to use in the chat for this channel.
     * @return Format of the channel
     */
    public String getFormat() {
        return format;
    }

    /***
     * Set the format to use in the chat for this channel.
     * @param format Format for the channel. eg: "{player}: {message}"
     */
    public void setFormat(@NotNull String format) {
        this.format = format;
    }

    /***
     * Get the world name of this channel, it will force player to use this channel when they enter into this world.
     * @return Name of the world
     */
    public String getSetAutoWorld() {
        return setAutoWorld;
    }

    /***
     * Set the world name of this channel, it will force player to use this channel when they enter into this world.
     * @param setAutoWorld Name of a world
     */
    public void setSetAutoWorld(@NotNull String setAutoWorld) {
        this.setAutoWorld = setAutoWorld;
    }

    /***
     * Know if this channel must be delete after reloading the config.
     * In this plugin, this method should return true because all channels are recreated from the config everytime you
     * reload the config.
     * In the case you used this plugin as an API, you should return false and update your channel when you want to avoid
     * this plugin to remove it.
     * @return If channel must be deleted after a config changes.
     */
    public boolean isDelete() {
        return delete;
    }

    /***
     * Set if this channel must be delete after reloading the config.
     * In this plugin, this method should return true because all channels are recreated from the config everytime you
     * reload the config.
     * In the case you used this plugin as an API, you should return false and update your channel when you want to avoid
     * this plugin to remove it.
     * @param delete True, channel must be deleted after a config changes (from this plugin).
     */
    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    /**
     * Get the override symbol of this channel, it will enforce the message to be sent to this channel if it starts with this symbol.
     * @return Symbol to override players current channel
     */
	public String getOverrideSymbol() {
		return overrideSymbol;
	}

	/**
     * Set the override symbol of this channel, it will enforce the message to be sent to this channel if it starts with this symbol.
     * @return overrideSymbol Symbol to override players current channel
     */	
	public void setOverrideSymbol(@NotNull String overrideSymbol) {
        this.overrideSymbol = overrideSymbol;
	}

    /**
     * Get custom data for this channel using HashMap as key;value
     * @return
     */
    public HashMap<String, Object> getCustom() {
        return custom;
    }

    /**
     * Set custom data for this channel using HashMap as key;value
     * @param custom
     */
    public void setCustom(HashMap<String, Object> custom) {
        this.custom = custom;
    }
}
