package be.kakumi.kachat.listeners;

import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.exceptions.CheckerException;
import be.kakumi.kachat.models.Channel;
import be.kakumi.kachat.utils.Checker;
import be.kakumi.kachat.utils.Formatter;
import be.kakumi.kachat.utils.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class SendMessageListener implements Listener {
    @EventHandler
    public void onSendMessage(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();
        Channel channel = KAChatAPI.getInstance().getPlayerChannel(player);
        String message = event.getMessage();
        try {
            //Format the message only
            for(Formatter formatter : KAChatAPI.getInstance().getMessageFormatters()) {
                message = formatter.format(player, message);
            }
            //Check if message is valid
            boolean toSend = checkMessage(player, message);
            //Store the message
            KAChatAPI.getInstance().updateLastMessage(player, message);

            //Here to be sure we have the format and the message, even if KAChatAPI Formatter is not loaded
            message = channel.getFormat().replace("{message}", message);
            //Replace place holder for the whole message and format
            for(Placeholder placeholder : KAChatAPI.getInstance().getPlaceholders()) {
                message = placeholder.format(player, message);
            }

            List<Player> receivers = KAChatAPI.getInstance().getChatManager().getValidReceivers(channel, player);
            if (toSend) {
                KAChatAPI.getInstance().getChatManager().sendMessage(message, receivers);
            }

            if (KAChatAPI.getInstance().getChatSaver() != null) {
                KAChatAPI.getInstance().getChatSaver().addMessage(message, toSend);
            }
        } catch (CheckerException e) {
            player.sendMessage("§c" + e.getMessage());
        } catch (Exception e2) {
            player.sendMessage("§cAn error occured, please check the console.");
            e2.printStackTrace();
        }
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
