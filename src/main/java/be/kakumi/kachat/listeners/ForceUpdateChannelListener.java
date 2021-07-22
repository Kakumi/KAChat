package be.kakumi.kachat.listeners;

import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.models.Channel;
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
            KAChatAPI.getInstance().setPlayerChannel(event.getPlayer(), KAChatAPI.getInstance().getDefaultChannel());
            event.getPlayer().sendMessage("§cYour chat channel has been automatically set to default because you used one reserved to the world before the teleportation.");
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
                KAChatAPI.getInstance().setPlayerChannel(player, channelWorld);
                player.sendMessage("§aYour channel has been updated to §f" + channelWorld.getCommand() + " §abecause you are into the world §f" + world + "§a.");
            }
        }
    }
}
