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

public class ChannelCmd implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 0) {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                player.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.CHANNEL_SET_DEFAULT));
                KAChatAPI.getInstance().removePlayerChannel(player, PlayerChangeChannelReason.COMMAND);
            } else {
                commandSender.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.MUST_BE_A_PLAYER));
            }
        } else {
            Channel channel = KAChatAPI.getInstance().getChannelFromCommand(strings[0]);
            Player playerToChange = null;
            PlayerChangeChannelReason reason;
            String fullCommand = StringUtils.join(strings, " ");
            boolean force = Pattern.matches(".*\\b[-f]\\b.*", fullCommand);
            boolean forcePermission = force || fullCommand.contains("-fp");
            boolean forceWorld = force || fullCommand.contains("-fw");

            if ((force || forcePermission || forceWorld) && !commandSender.hasPermission("kachat.cmd.channel.force")) {
                commandSender.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.NO_PERMISSION_FORCE_TAGS));
            }

            if (strings.length >= 2) {
                reason = PlayerChangeChannelReason.COMMAND_OTHERS;
                if (commandSender.hasPermission("kachat.cmd.channel.others")) {
                    playerToChange = Bukkit.getServer().getPlayer(strings[1]);
                    if (playerToChange == null) {
                        commandSender.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.CHANNEL_NOT_CONNECTED, strings[1], null));
                    }
                } else {
                    commandSender.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.CHANNEL_NO_PERMISSION_SET));
                }
            } else {
                reason = PlayerChangeChannelReason.COMMAND;
                if (commandSender instanceof Player) {
                    playerToChange = (Player) commandSender;
                } else {
                    commandSender.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.MUST_BE_A_PLAYER));
                }
            }

            if (playerToChange != null) {
                if (channel == null) {
                    commandSender.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.CHANNEL_DOESNT_EXIST));
                    StringBuilder available = new StringBuilder();
                    for (Channel channelNext : KAChatAPI.getInstance().getChannels()) {
                        if (channelNext.isListed()) {
                            if (!available.toString().equals("")) {
                                available.append("§c§l - ");
                            }
                            available.append("§e").append(channelNext.getCommand());
                        }
                    }
                    commandSender.sendMessage(available.toString());
                } else {
                    if (forcePermission || channel.hasPermissionToUse(playerToChange)) {
                        if (forceWorld || channel.getWorld().equals("") || playerToChange.getWorld().getName().equalsIgnoreCase(channel.getWorld())) {
                            if (playerToChange != commandSender) {
                                commandSender.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.CHANNEL_SET_PLAYER, playerToChange.getName(), channel.getCommand()));
                            }
                            playerToChange.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.CHANNEL_SET_MYSELF, channel.getCommand(), null));
                            KAChatAPI.getInstance().setPlayerChannel(playerToChange, channel, reason);
                        } else {
                            if (playerToChange == commandSender) {
                                commandSender.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.CHANNEL_WRONG_WORLD_MYSELF, channel.getWorld(), null));
                            } else {
                                commandSender.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.CHANNEL_WRONG_WORLD_PLAYER, playerToChange.getName(), channel.getWorld()));
                            }
                        }
                    } else {
                        if (playerToChange == commandSender) {
                            commandSender.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.CHANNEL_NO_PERMISSION_USE_MYSELF));
                        } else {
                            commandSender.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.CHANNEL_NO_PERMISSION_USE_PLAYER, playerToChange.getName(), null));
                        }
                    }
                }
            }
        }

        return true;
    }
}
