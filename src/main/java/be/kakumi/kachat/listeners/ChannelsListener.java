package be.kakumi.kachat.listeners;

import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.enums.PlayerChangeChannelReason;
import be.kakumi.kachat.events.GetChannelListEvent;
import be.kakumi.kachat.models.Channel;
import be.kakumi.kachat.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChannelsListener implements Listener {
    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (event.getView().getTitle().startsWith(KAChatAPI.getInstance().getMessageManager().get(MessageManager.INVENTORY_CHANNELS_TITLE, false))) {
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
                        List<Channel> tempChannels = KAChatAPI.getInstance().getChannels().stream().filter(Channel::isListed).collect(Collectors.toList());
                        GetChannelListEvent getChannelListEvent = new GetChannelListEvent(player, tempChannels);
                        Bukkit.getPluginManager().callEvent(getChannelListEvent);
                        List<Channel> channelList = getChannelListEvent.getChannels();

                        Channel channel = channelList.get(slot);
                        if (channel != null) {
                            if (channel.hasPermissionToUse(player)) {
                                if (channel.getWorld().equals("") || player.getWorld().getName().equalsIgnoreCase(channel.getWorld())) {
                                    player.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.CHANNEL_SET_MYSELF, Collections.singletonList(channel.getCommand())));
                                    KAChatAPI.getInstance().setPlayerChannel(player, channel, PlayerChangeChannelReason.COMMAND);
                                    player.closeInventory();
                                } else {
                                    player.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.CHANNEL_WRONG_WORLD_MYSELF, Collections.singletonList(channel.getWorld())));
                                }
                            } else {
                                player.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.CHANNEL_NO_PERMISSION_USE_MYSELF));
                            }
                        }
                    }
                }
            } else {
                ((Player) event.getWhoClicked()).sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.ERROR_OCCURRED));
            }
        }
    }
}
