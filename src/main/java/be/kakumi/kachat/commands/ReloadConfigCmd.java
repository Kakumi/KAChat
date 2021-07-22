package be.kakumi.kachat.commands;

import be.kakumi.kachat.KAChat;
import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.utils.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadConfigCmd implements CommandExecutor {
    private final KAChat main;
    public ReloadConfigCmd(KAChat main) {
        this.main = main;
    }

    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        main.reloadConfig();
        commandSender.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.RELOAD_CONFIG));

        return true;
    }
}
