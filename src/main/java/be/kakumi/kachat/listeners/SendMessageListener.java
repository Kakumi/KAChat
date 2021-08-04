package be.kakumi.kachat.listeners;

import be.kakumi.kachat.KAChat;
import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.events.ChannelReceiveMessageEvent;
import be.kakumi.kachat.exceptions.CheckerException;
import be.kakumi.kachat.models.Channel;
import be.kakumi.kachat.utils.Checker;
import be.kakumi.kachat.utils.Formatter;
import be.kakumi.kachat.utils.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class SendMessageListener implements Listener {
    private final KAChat main;
    public SendMessageListener(KAChat main) {
        this.main = main;
    }

    @EventHandler
    public void onSendMessage(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();
        String message = event.getMessage();
        Channel channel = getChannel(player, message);
        try {
            //Format the message only
            for(Formatter formatter : KAChatAPI.getInstance().getMessageFormatters()) {
                message = formatter.format(player, message);
            }
            //Check if message is valid
            boolean toSend = checkMessage(player, message);
            //Here to be sure we have the format and the message, even if KAChatAPI Formatter is not loaded
            String messageFormat = channel.getFormat().replace("{message}", message);
            //Replace placeholder for the whole message and format
            for(Placeholder placeholder : KAChatAPI.getInstance().getPlaceholders()) {
                messageFormat = placeholder.format(player, channel, messageFormat);
            }

            List<Player> receivers = KAChatAPI.getInstance().getChatManager().getValidReceivers(channel, player);

            if (toSend) {
                KAChatAPI.getInstance().getChatManager().sendMessage(messageFormat, receivers);
            }

            final String messageFormatFinal = messageFormat;
            final String messageFinal = message;
            //Because we can't run event from a asynchronous thread
            Bukkit.getScheduler().runTaskLater(main, () -> Bukkit.getPluginManager().callEvent(new ChannelReceiveMessageEvent(channel, player, receivers, messageFormatFinal, messageFinal, toSend)), 1);
        } catch (CheckerException e) {
            player.sendMessage(e.getMessage());
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    @EventHandler
    public void onChannelReceiveMessage(ChannelReceiveMessageEvent event) {
        KAChatAPI.getInstance().updateLastMessage(event.getSender(), event.getMessage());

        if (KAChatAPI.getInstance().getChatSaver() != null) {
            KAChatAPI.getInstance().getChatSaver().addMessage(event.getMessageFormat(), event.isPosted());
        }
    }
    
    private Channel getChannel(Player player, String message) {
    	Channel channel = KAChatAPI.getInstance().getChannelFromMessage(message);
        
        if (channel != null) {
        	return channel;
        }
        
        return KAChatAPI.getInstance().getPlayerChannel(player);
    }

    private boolean checkMessage(Player player, String message) throws CheckerException {
        boolean toSend = true;

        for(Checker checker : KAChatAPI.getInstance().getCheckers()) {
            if (toSend && !checker.valid(player, message)) {
                toSend = false;
            }
        }

        return toSend;
    }
}
