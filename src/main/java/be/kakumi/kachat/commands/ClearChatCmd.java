package be.kakumi.kachat.commands;

import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.enums.PlayerChangeChannelReason;
import be.kakumi.kachat.models.Channel;
import be.kakumi.kachat.utils.MessageManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.regex.Pattern;

public class ClearChatCmd implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        String fullCommand = StringUtils.join(strings, " ");
        boolean force = Pattern.matches(".*\\b[-a]\\b.*", fullCommand);

        if (force && !commandSender.hasPermission("kachat.cmd.clearchat.all")) {
            commandSender.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.NO_PERMISSION_FORCE_TAGS));
            return true;
        }

        if (force) {
            Bukkit.broadcastMessage(StringUtils.repeat(" \n", 100));
            commandSender.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.CLEAR_CHAT_ALL));
        } else if (commandSender instanceof Player) {
            for (int i = 0; i < 100; i++) {
                commandSender.sendMessage(" \n");
            }
            commandSender.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.CLEAR_CHAT));
        } else {
            commandSender.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.MUST_BE_A_PLAYER));
        }

        return true;
    }
}
