package be.kakumi.kachat.listeners;

import be.kakumi.kachat.KAChat;
import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.enums.MessageNotSendReason;
import be.kakumi.kachat.events.ChannelPreSendEvent;
import be.kakumi.kachat.events.ChannelReceiveMessageEvent;
import be.kakumi.kachat.events.MessageNotSendEvent;
import be.kakumi.kachat.exceptions.CheckerException;
import be.kakumi.kachat.models.Channel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class SendMessageListener implements Listener {
    private final KAChat main;
    public SendMessageListener(KAChat main) {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSendMessage(AsyncPlayerChatEvent event) {
        if (!event.isCancelled()) {
            Player player = event.getPlayer();
            String message = event.getMessage();
            Channel channel = KAChatAPI.getInstance().getPlayerChannel(player, message);

            try {
                List<Player> receivers = KAChatAPI.getInstance().getChatManager().getValidReceivers(channel, player);
                String finalMessage = KAChatAPI.getInstance().getChatManager().getMessage(player, message, channel);

                ChannelPreSendEvent preSendEvent = new ChannelPreSendEvent(channel, player, receivers, finalMessage, message);
                Bukkit.getPluginManager().callEvent(preSendEvent);
                if (!preSendEvent.isCancelled()) {
                    event.getRecipients().clear();
                    event.getRecipients().addAll(receivers);
                    event.setMessage(finalMessage);
                    event.setFormat("%2$s"); //only display message

                    Bukkit.getPluginManager().callEvent(new ChannelReceiveMessageEvent(channel, player, receivers, finalMessage, message));
                } else {
                    MessageNotSendEvent notSendEvent = new MessageNotSendEvent(player, channel, MessageNotSendReason.PLUGIN);
                    Bukkit.getPluginManager().callEvent(notSendEvent);
                    event.setCancelled(true);
                }
            } catch (CheckerException e) {
                MessageNotSendEvent notSendEvent = new MessageNotSendEvent(player, channel, MessageNotSendReason.SECURITY);
                Bukkit.getPluginManager().callEvent(notSendEvent);

                player.sendMessage(e.getMessage());
                event.setCancelled(true);
            } catch (Exception e2) {
                e2.printStackTrace();
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onChannelReceiveMessage(ChannelReceiveMessageEvent event) {
        KAChatAPI.getInstance().updateLastMessage(event.getSender(), event.getMessage());

        if (KAChatAPI.getInstance().getChatSaver() != null) {
            KAChatAPI.getInstance().getChatSaver().addMessage(event.getMessageFormat());
        }
    }
}
