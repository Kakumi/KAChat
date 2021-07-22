package be.kakumi.kachat.listeners;

import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.enums.PlayerChangeChannelReason;
import be.kakumi.kachat.models.Channel;
import be.kakumi.kachat.utils.MessageManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Iterator;
import java.util.List;

public class ForceUpdateChannelListener implements Listener {
    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Channel channel = KAChatAPI.getInstance().getPlayerChannel(event.getPlayer());
        if (!channel.getWorld().equals("") && event.getTo() != null && event.getFrom().getWorld() != event.getTo().getWorld()) {
            KAChatAPI.getInstance().setPlayerChannel(event.getPlayer(), KAChatAPI.getInstance().getDefaultChannel(), PlayerChangeChannelReason.WORLD_RESTRICTED);
            event.getPlayer().sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.CHANNEL_WORLD_RESTRICTED));
        }

        if (event.getTo() != null && event.getTo().getWorld() != null) {
            forceChannelInsideWorld(event.getPlayer(), event.getTo().getWorld().getName());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getLocation().getWorld() != null) {
            forceChannelInsideWorld(event.getPlayer(), event.getPlayer().getLocation().getWorld().getName());
        }
    }

    private void forceChannelInsideWorld(Player player, String world) {
        boolean updated = false;
        List<Channel> channelsWorld = KAChatAPI.getInstance().getChannelsFromWorld(world);
        Iterator<Channel> iterator = channelsWorld.iterator();

        while(!updated && iterator.hasNext()) {
            Channel channelWorld = iterator.next();
            if (channelWorld.hasPermissionToUse(player)) {
                updated = true;
                KAChatAPI.getInstance().setPlayerChannel(player, channelWorld, PlayerChangeChannelReason.AUTO_WORLD);
                player.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.CHANNEL_AUTO_WORLD, channelWorld.getCommand(), world));
            }
        }
    }
}
