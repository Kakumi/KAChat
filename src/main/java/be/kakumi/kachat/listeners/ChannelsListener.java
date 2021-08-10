package be.kakumi.kachat.listeners;

import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.enums.PlayerChangeChannelReason;
import be.kakumi.kachat.models.Channel;
import be.kakumi.kachat.utils.MessageManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChannelsListener implements Listener {
    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (event.getView().getTitle().startsWith(KAChatAPI.getInstance().getMessageManager().get(MessageManager.INVENTORY_CHANNELS_TITLE))) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            int slot = event.getSlot();
            Pattern pattern = Pattern.compile("(\\d*)/\\d+");
            Matcher matcher = pattern.matcher(event.getView().getTitle().replaceAll("[§&].", ""));

            if (matcher.find()) {
                int page = Integer.parseInt(matcher.group(1));

                if (event.getCurrentItem() != null) {
                    if (event.getCurrentItem().getType() == Material.ARROW) {
                        if (slot % 9 == 0) {
                            player.performCommand("channels " + (page - 1));
                        } else if (slot % 9 == 8) {
                            player.performCommand("channels " + (page + 1));
                        }
                    } else {
                        Channel channel = KAChatAPI.getInstance().getChannels().get(slot);
                        if (channel != null) {
                            if (channel.hasPermissionToUse(player)) {
                                if (channel.getWorld().equals("") || player.getWorld().getName().equalsIgnoreCase(channel.getWorld())) {
                                    player.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.CHANNEL_SET_MYSELF, channel.getCommand(), null));
                                    KAChatAPI.getInstance().setPlayerChannel(player, channel, PlayerChangeChannelReason.COMMAND);
                                } else {
                                    player.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.CHANNEL_WRONG_WORLD_MYSELF, channel.getWorld(), null));
                                }
                            } else {
                                player.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.CHANNEL_NO_PERMISSION_USE_MYSELF));
                            }
                        }
                    }
                }
            } else {
                event.getWhoClicked().sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.ERROR_OCCURRED));
            }
        }
    }
}
