package be.kakumi.kachat.events;

import be.kakumi.kachat.enums.MessageNotSendReason;
import be.kakumi.kachat.models.Channel;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MessageNotSendEvent extends Event {
    private final static HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final Channel channel;
    private final MessageNotSendReason reason;

    public MessageNotSendEvent(@NotNull Player who, Channel channel, MessageNotSendReason reason) {
        super(true);
        this.player = who;
        this.channel = channel;
        this.reason = reason;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return player;
    }

    public Channel getChannel() {
        return channel;
    }

    public MessageNotSendReason getReason() {
        return reason;
    }
}
