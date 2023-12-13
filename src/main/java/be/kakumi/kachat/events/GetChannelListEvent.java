package be.kakumi.kachat.events;

import be.kakumi.kachat.models.Channel;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GetChannelListEvent extends PlayerEvent {
    private final static HandlerList HANDLERS = new HandlerList();
    private final List<Channel> channels;

    public GetChannelListEvent(Player player, List<Channel> channels) {
        super(player);
        this.channels = channels;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
