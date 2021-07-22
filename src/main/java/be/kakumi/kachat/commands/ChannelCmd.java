package be.kakumi.kachat.commands;

import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.enums.PlayerChangeChannelReason;
import be.kakumi.kachat.models.Channel;
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
                player.sendMessage("§aYou set your channel to the default one.");
                KAChatAPI.getInstance().removePlayerChannel(player, PlayerChangeChannelReason.COMMAND);
            } else {
                commandSender.sendMessage("§cYou must be a player to execute that command.");
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
                commandSender.sendMessage("§cYou don't have the permission to use force tag.");
            }

            if (strings.length >= 2) {
                reason = PlayerChangeChannelReason.COMMAND_OTHERS;
                if (commandSender.hasPermission("kachat.cmd.channel.others")) {
                    playerToChange = Bukkit.getServer().getPlayer(strings[1]);
                    if (playerToChange == null) {
                        commandSender.sendMessage("§cYou can't change the channel of §f" + strings[1] + " §cbecause he is not connected.");
                    }
                } else {
                    commandSender.sendMessage("§cYou don't have the permission to set the channel for a player.");
                }
            } else {
                reason = PlayerChangeChannelReason.COMMAND;
                if (commandSender instanceof Player) {
                    playerToChange = (Player) commandSender;
                } else {
                    commandSender.sendMessage("§cYou must be a player to execute that command.");
                }
            }

            if (playerToChange != null) {
                if (channel == null) {
                    commandSender.sendMessage("§cThis channel doesn't exist, channels available :");
                    StringBuilder available = new StringBuilder();
                    Iterator<Channel> iterator = KAChatAPI.getInstance().getChannels().iterator();
                    while(iterator.hasNext()) {
                        Channel channelNext = iterator.next();
                        if (channelNext.isListed()) {
                            if (!available.toString().equals("")) {
                                available.append("§c§l - ");
                            }
                            available.append("§e").append(iterator.next().getCommand());
                        }
                    }
                    commandSender.sendMessage(available.toString());
                } else {
                    if (forcePermission || channel.hasPermissionToUse(playerToChange)) {
                        if (forceWorld || channel.getWorld().equals("") || playerToChange.getWorld().getName().equalsIgnoreCase(channel.getWorld())) {
                            if (playerToChange != commandSender) {
                                commandSender.sendMessage("§aYou change the channel of §f" + playerToChange.getName() + " §ato §f" + channel.getCommand() + "§a.");
                            }
                            playerToChange.sendMessage("§aYour channel is set to §f" + channel.getCommand() + "§a.");
                            KAChatAPI.getInstance().setPlayerChannel(playerToChange, channel, reason);
                        } else {
                            if (playerToChange == commandSender) {
                                commandSender.sendMessage("§cYou can't use this channel because you are not in the world §f" + channel.getWorld() + "§c.");
                            } else {
                                commandSender.sendMessage(playerToChange.getName() + " §ccan't use this channel because he is not in the world §f" + channel.getWorld() + "§c.");
                            }
                        }
                    } else {
                        if (playerToChange == commandSender) {
                            commandSender.sendMessage("§cYou don't have the permission to use this channel.");
                        } else {
                            commandSender.sendMessage(playerToChange.getName() + " §cdon't have the permission to use this channel.");
                        }
                    }
                }
            }
        }

        return true;
    }
}
