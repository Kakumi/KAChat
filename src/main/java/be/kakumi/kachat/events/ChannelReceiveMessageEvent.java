package be.kakumi.kachat.events;

import be.kakumi.kachat.models.Channel;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChannelReceiveMessageEvent extends Event {
    private final static HandlerList HANDLERS = new HandlerList();
    private final Channel channel;
    private final Player sender;
    private final List<Player> receivers;
    private final String messageFormat;
    private final String message;

    public ChannelReceiveMessageEvent(Channel channel, Player sender, List<Player> receivers, String messageFormat, String message) {
        super(true);
        this.channel = channel;
        this.sender = sender;
        this.receivers = receivers;
        this.messageFormat = messageFormat;
        this.message = message;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Channel getChannel() {
        return channel;
    }

    public Player getSender() {
        return sender;
    }

    public List<Player> getReceivers() {
        return receivers;
    }

    public String getMessageFormat() {
        return messageFormat;
    }

    public String getMessage() {
        return message;
    }
}
