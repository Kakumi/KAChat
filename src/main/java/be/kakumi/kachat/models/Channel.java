package be.kakumi.kachat.models;

import org.bukkit.entity.Player;

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
    }

    public Channel(String prefix, String command) {
        this(prefix, command, "");
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public boolean isListed() {
        return listed;
    }

    public void setListed(boolean listed) {
        this.listed = listed;
    }

    public Enum getChannelType() {
        return channelType;
    }

    public void setChannelType(Enum channelType) {
        this.channelType = channelType;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public String getPermissionToUse() {
        return permissionToUse;
    }

    public void setPermissionToUse(String permissionToUse) {
        this.permissionToUse = permissionToUse;
    }

    public boolean hasPermissionToUse(Player player) {
        return this.permissionToUse.equals("") || player.hasPermission(this.permissionToUse);
    }

    public String getPermissionToSee() {
        return permissionToSee;
    }

    public void setPermissionToSee(String permissionToSee) {
        this.permissionToSee = permissionToSee;
    }

    public boolean hasPermissionToSee(Player player) {
        return this.permissionToSee.equals("") || player.hasPermission(this.permissionToSee);
    }

    public boolean isForInside() {
        return forInside;
    }

    public void setForInside(boolean forInside) {
        this.forInside = forInside;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getSetAutoWorld() {
        return setAutoWorld;
    }

    public void setSetAutoWorld(String setAutoWorld) {
        this.setAutoWorld = setAutoWorld;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }
}
