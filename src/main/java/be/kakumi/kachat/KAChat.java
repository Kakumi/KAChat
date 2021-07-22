package be.kakumi.kachat;

import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.api.PlaceholderAPI;
import be.kakumi.kachat.api.VaultAPI;
import be.kakumi.kachat.commands.ChannelCmd;
import be.kakumi.kachat.commands.ReloadConfigCmd;
import be.kakumi.kachat.exceptions.AddChannelException;
import be.kakumi.kachat.listeners.ForceUpdateChannelListener;
import be.kakumi.kachat.listeners.SendMessageListener;
import be.kakumi.kachat.middlewares.message.ColorFormatter;
import be.kakumi.kachat.middlewares.message.GrammarDot;
import be.kakumi.kachat.middlewares.message.GrammarUppercase;
import be.kakumi.kachat.middlewares.message.MentionPlayer;
import be.kakumi.kachat.middlewares.security.*;
import be.kakumi.kachat.models.Channel;
import be.kakumi.kachat.timers.ChatSaverRunnable;
import be.kakumi.kachat.utils.ChatSaver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class KAChat extends JavaPlugin {
    private static final String PREFIX = "§6[§bKAChat§6]";
    private ChatSaverRunnable timer;

    @Override
    public void onEnable() {
        super.onEnable();
        saveDefaultConfig();

        loadListeners();
        loadCommands();

        //-- Load from reloadConfig
        //loadChannels();
        //loadCheckers();
        //loadMessageFormatters();
        loadChatSaver();

        loadDependencies();
    }

    private void loadDependencies() {
        Log("§aDependencies available:");
        if (loadPlugin("PlaceholderAPI", false)) {
            KAChatAPI.getInstance().getPlaceholders().add(new PlaceholderAPI());
        }
        if (loadPlugin("Vault", false)) {
            KAChatAPI.getInstance().getPlaceholders().add(new VaultAPI(this));
        }
    }

    private boolean loadPlugin(String plugin, boolean depend) {
        if (Bukkit.getPluginManager().getPlugin(plugin) != null) {
            Log("\t§a- " + plugin + " (loaded)");
            return true;
        }

        if (depend) {
            Error("Can't load the plugin because you need the plugin : " + plugin);
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            Log("\t§c- " + plugin + " (not found)");
        }

        return false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        stopChatSaver();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        loadChannels();
        loadCheckers();
        loadMessageFormatters();
        loadChatSaver();
    }

    private void loadListeners() {
        getServer().getPluginManager().registerEvents(new SendMessageListener(), this);
        getServer().getPluginManager().registerEvents(new ForceUpdateChannelListener(), this);
    }

    @SuppressWarnings("ConstantConditions")
    private void loadCommands() {
        getCommand("channel").setExecutor(new ChannelCmd());
        getCommand("kareload").setExecutor(new ReloadConfigCmd(this));
    }

    @SuppressWarnings("ConstantConditions")
    private void loadChannels() {
        KAChatAPI.getInstance().clearChannels();

        String defaultFormat = getConfig().getString("chat.format");
        String defaultChannelCode = getConfig().getString("chat.defaultChannel");
        Channel defaultChannel = null;

        for(String name : getConfig().getConfigurationSection("chat.channels").getKeys(false)) {
            if (getConfig().getBoolean("chat.channels." + name + ".enable")) {
                Channel channel = new Channel(
                        getConfig().getString("chat.channels." + name + ".prefix"),
                        getConfig().getString("chat.channels." + name + ".command")
                );

                channel.setListed(getConfig().getBoolean("chat.channels." + name + ".listed"));
                channel.setColor(getConfig().getString("chat.channels." + name + ".color"));
                channel.setCooldown(getConfig().getInt("chat.channels." + name + ".cooldown"));
                channel.setForInside(getConfig().getBoolean("chat.channels." + name + ".insideRestriction"));
                channel.setWorld(getConfig().getString("chat.channels." + name + ".world"));
                channel.setRange(getConfig().getInt("chat.channels." + name + ".range"));
                channel.setPermissionToUse(getConfig().getString("chat.channels." + name + ".permissionToUse"));
                channel.setPermissionToSee(getConfig().getString("chat.channels." + name + ".permissionToSee"));
                channel.setSetAutoWorld(getConfig().getString("chat.channels." + name + ".setAutoWorld"));
                channel.setDelete(true);
                if (getConfig().isSet("chat.channels." + name + ".format")) {
                    channel.setFormat(getConfig().getString("chat.channels." + name + ".format"));
                } else {
                    channel.setFormat(defaultFormat);
                }

                if (name.equalsIgnoreCase(defaultChannelCode)) {
                    defaultChannel = channel;
                }

                try {
                    //If it exist (after config reload) we override it, maybe there is others channels from others plugins.
                    //That's why we don't clear the list after the reload.
                    if (KAChatAPI.getInstance().getChannelFromCommand(channel.getCommand()) != null) {
                        KAChatAPI.getInstance().removeChannel(channel.getCommand());
                    }
                    KAChatAPI.getInstance().addChannel(channel);
                } catch (AddChannelException e) {
                    Error("Unable to use channel " + name + " : " + e.getMessage());
                }
            }
        }

        //Atfer reload, if players are using a deleted channel delete we remove from the list, else, we update in case that
        //the channel has been updated in the config file.
        for(Map.Entry<UUID, Channel> entry : KAChatAPI.getInstance().getPlayersChannel().entrySet()) {
            Channel newChannel = KAChatAPI.getInstance().getChannelFromCommand(entry.getValue().getCommand());
            KAChatAPI.getInstance().getPlayersChannel().remove(entry.getKey());
            if (newChannel == null) {
                Player playerFound = Bukkit.getServer().getPlayer(entry.getKey());
                if (playerFound != null) {
                    playerFound.sendMessage("§cYour channel has been set to default because the last one has been deleted.");
                }
            } else {
                KAChatAPI.getInstance().getPlayersChannel().put(entry.getKey(), newChannel);
            }
        }

        if (defaultChannel == null || defaultFormat == null) {
            Error("You must set a default channel and default format in the config file, but you can change it later using the KAChat API.");
            this.getPluginLoader().disablePlugin(this);
        } else {
            KAChatAPI.getInstance().setDefaultChannel(defaultChannel);
            KAChatAPI.getInstance().setDefaultFormat(defaultFormat);

            Log("§f" + KAChatAPI.getInstance().getChannels().size() + " §achannels registered.");
        }
    }

    private void loadCheckers() {
        KAChatAPI.getInstance().clearCheckers();

        if (getConfig().getBoolean("security.antiCapslock.enable")) {
            KAChatAPI.getInstance().getCheckers().add(new AntiCapsLock(getConfig().getInt("security.antiCapslock.percent")));
        }
        if (getConfig().getBoolean("security.antiAds")) {
            KAChatAPI.getInstance().getCheckers().add(new AntiAdvertisement());
        }
        KAChatAPI.getInstance().getCheckers().add(new AntiBadWords(getConfig().getStringList("security.badWords")));
        KAChatAPI.getInstance().getCheckers().add(new GrammarMinSize(getConfig().getInt("grammar.minSize")));
        KAChatAPI.getInstance().getCheckers().add(new CooldownMessage());
        if (getConfig().getBoolean("security.antiSpam.enable")) {
            KAChatAPI.getInstance().getCheckers().add(new AntiSpam(getConfig().getInt("security.antiSpam.max")));
        }
        KAChatAPI.getInstance().getCheckers().add(new ChatProtect());

        Log("§f" + KAChatAPI.getInstance().getCheckers().size() + " §acheckers registered.");
    }

    private void loadMessageFormatters() {
        KAChatAPI.getInstance().clearMessageFormatters();

        if (getConfig().getBoolean("grammar.capitaliseFirstLetter")) {
            KAChatAPI.getInstance().getMessageFormatters().add(new GrammarUppercase());
        }
        if (getConfig().getBoolean("grammar.forceDotAtTheEnd")) {
            KAChatAPI.getInstance().getMessageFormatters().add(new GrammarDot());
        }
        if (getConfig().getBoolean("notification.mention.enable")) {
            KAChatAPI.getInstance().getMessageFormatters().add(new MentionPlayer(
                    getConfig().getString("notification.mention.symbolToUse"),
                    getConfig().getString("notification.mention.symbolToSee"),
                    getConfig().getString("notification.mention.color"),
                    getConfig().getBoolean("notification.mention.sound")
            ));
        }
        KAChatAPI.getInstance().getMessageFormatters().add(new ColorFormatter());

        Log("§f" + KAChatAPI.getInstance().getMessageFormatters().size() + " §aformatters registered.");
    }

    private void loadChatSaver() {
        if (getConfig().getBoolean("security.save.enable")) {
            ChatSaver chatSaver = new ChatSaver(this, "logs/chat");
            KAChatAPI.getInstance().setChatSaver(chatSaver);
            int internal = getConfig().getInt("security.save.interval") * 20;

            timer = new ChatSaverRunnable(this);
            timer.runTaskTimer(this, internal, internal);
        } else {
            stopChatSaver();
        }
    }

    private void stopChatSaver() {
        if (timer != null) {
            timer.cancel();
        }

        if (KAChatAPI.getInstance().getChatSaver() != null) {
            try {
                KAChatAPI.getInstance().getChatSaver().saveMessages();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                KAChatAPI.getInstance().getChatSaver().rename();
                KAChatAPI.getInstance().setChatSaver(null);
            }
        }
    }

    public void Log(String message) {
        getServer().getConsoleSender().sendMessage(PREFIX + " §f" + message);
    }

    public void Error(String message) {
        Log("§4[§cERROR§4] §c" + message);
    }

    public void Warn(String message) {
        Log("§6[§eWARNING§6] §c" + message);
    }
}
