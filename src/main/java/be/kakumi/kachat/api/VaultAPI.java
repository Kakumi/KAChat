package be.kakumi.kachat.api;

import be.kakumi.kachat.KAChat;
import be.kakumi.kachat.models.Channel;
import be.kakumi.kachat.utils.Placeholder;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultAPI implements Placeholder {
    private final KAChat main;
    private boolean economyLoaded;
    private boolean permissionLoaded;
    private boolean chatLoaded;
    private Economy economy;
    private Permission permission;
    private Chat chat;

    public VaultAPI(KAChat main) {
        this.main = main;
        setupEconomy();
        setupChat();
        setupPermissions();
    }

    private void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = main.getServer().getServicesManager().getRegistration(Economy.class);
        economyLoaded = rsp != null;

        if (economyLoaded) {
            economy = rsp.getProvider();
        }
    }

    private void setupChat() {
        RegisteredServiceProvider<Chat> rsp = main.getServer().getServicesManager().getRegistration(Chat.class);
        chatLoaded = rsp != null;

        if (chatLoaded) {
            chat = rsp.getProvider();
        }
    }

    private void setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = main.getServer().getServicesManager().getRegistration(Permission.class);
        permissionLoaded = rsp != null;

        if (permissionLoaded) {
            permission = rsp.getProvider();
        }
    }

    public String format(Player player, Channel channel, String message) {
        //Economy
        if (economyLoaded && economy.isEnabled()) {
            message = message.replace("{vault_eco_name}", economy.getName());
            message = message.replace("{vault_eco_money}", economy.getBalance(player) + "");
            message = message.replace("{vault_eco_money.2f}", String.format("%.1f", economy.getBalance(player)));
            message = message.replace("{vault_eco_currency_name}", economy.currencyNameSingular());
            message = message.replace("{vault_eco_currency_name_plural}", economy.currencyNamePlural());
        }

        //Chat
        if (chatLoaded && chat.isEnabled()) {
            message = message.replace("{vault_chat_name}", chat.getName());
            message = message.replace("{vault_chat_player_prefix}", chat.getPlayerPrefix(player));
            message = message.replace("{vault_chat_player_suffix}", chat.getPlayerSuffix(player));
            message = message.replace("{vault_chat_player_primary_group}", chat.getPrimaryGroup(player));
        }

        //Permission
        if (permissionLoaded && permission.isEnabled() && permission.hasSuperPermsCompat() && permission.hasGroupSupport()) {
            message = message.replace("{vault_perm_group}", permission.getPrimaryGroup(player));
        }

        return message;
    }
}
