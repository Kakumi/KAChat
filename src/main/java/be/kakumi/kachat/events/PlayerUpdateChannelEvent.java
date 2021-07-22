package be.kakumi.kachat.events;

import be.kakumi.kachat.enums.PlayerChangeChannelReason;
import be.kakumi.kachat.models.Channel;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerUpdateChannelEvent extends Event {
    private final static HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final Channel fromChannel;
    private final Channel toChannel;
    private final PlayerChangeChannelReason reason;

    public PlayerUpdateChannelEvent(Player player, Channel fromChannel, Channel toChannel, PlayerChangeChannelReason reason) {
        this.player = player;
        this.fromChannel = fromChannel;
        this.toChannel = toChannel;
        this.reason = reason;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return player;
    }

    public Channel getFromChannel() {
        return fromChannel;
    }

    public Channel getToChannel() {
        return toChannel;
    }

    public PlayerChangeChannelReason getReason() {
        return reason;
    }
}
