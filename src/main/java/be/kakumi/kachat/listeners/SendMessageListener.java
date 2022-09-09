package be.kakumi.kachat.listeners;

import be.kakumi.kachat.KAChat;
import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.events.ChannelReceiveMessageEvent;
import be.kakumi.kachat.models.Channel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class SendMessageListener implements Listener {
    private final KAChat main;
    public SendMessageListener(KAChat main) {
        this.main = main;
    }

    @EventHandler
    public void onSendMessage(AsyncPlayerChatEvent event) {
        if (!event.isCancelled()) {
            event.setCancelled(true);

            Player player = event.getPlayer();
            String message = event.getMessage();
            Channel channel = KAChatAPI.getInstance().getPlayerChannel(player, message);
            KAChatAPI.getInstance().getChatManager().sendMessage(player, message, channel);
        }
    }

    @EventHandler
    public void onChannelReceiveMessage(ChannelReceiveMessageEvent event) {
        KAChatAPI.getInstance().updateLastMessage(event.getSender(), event.getMessage());

        if (KAChatAPI.getInstance().getChatSaver() != null) {
            KAChatAPI.getInstance().getChatSaver().addMessage(event.getMessageFormat(), event.isPosted());
        }
    }
}
