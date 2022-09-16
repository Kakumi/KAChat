package be.kakumi.kachat;

import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.api.PlaceholderAPI;
import be.kakumi.kachat.api.VaultAPI;
import be.kakumi.kachat.commands.ChannelCmd;
import be.kakumi.kachat.commands.ChannelsCmd;
import be.kakumi.kachat.commands.ClearChatCmd;
import be.kakumi.kachat.commands.ReloadConfigCmd;
import be.kakumi.kachat.enums.PlayerChangeChannelReason;
import be.kakumi.kachat.events.PlayerUpdateChannelEvent;
import be.kakumi.kachat.exceptions.AddChannelException;
import be.kakumi.kachat.exceptions.MessagesFileException;
import be.kakumi.kachat.listeners.ChannelsListener;
import be.kakumi.kachat.listeners.ForceUpdateChannelListener;
import be.kakumi.kachat.listeners.SendMessageListener;
import be.kakumi.kachat.middlewares.message.*;
import be.kakumi.kachat.middlewares.security.*;
import be.kakumi.kachat.models.Channel;
import be.kakumi.kachat.models.PlayerTextHover;
import be.kakumi.kachat.timers.ChatSaverRunnable;
import be.kakumi.kachat.utils.ChatSaver;
import be.kakumi.kachat.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class KAChat extends JavaPlugin {
    public static KAChat instance;
    private ChatSaverRunnable timer;

    public static KAChat getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        super.onEnable();
        saveDefaultConfig();
        reloadConfig();

        loadListeners();
        loadCommands();

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

    //Load when using the config
    @Override
    public void reloadConfig() {
        super.reloadConfig();
        if (loadMessageManager()) {
            loadChannels();
            loadCheckers();
            loadMessageFormatters();
            loadChatSaver();
            loadTextHover();
        }
    }

    private boolean loadMessageManager() {
        try {
            MessageManager messageManager = new MessageManager(this);
            KAChatAPI.getInstance().setMessageManager(messageManager);

            return true;
        } catch (MessagesFileException e) {
            Error(e.getMessage());
            getPluginLoader().disablePlugin(this);
            return false;
        }
    }

    private void loadListeners() {
        getServer().getPluginManager().registerEvents(new SendMessageListener(this), this);
        getServer().getPluginManager().registerEvents(new ForceUpdateChannelListener(), this);
        getServer().getPluginManager().registerEvents(new ChannelsListener(), this);
    }

    @SuppressWarnings("ConstantConditions")
    private void loadCommands() {
        getCommand("channel").setExecutor(new ChannelCmd());
        getCommand("channel").setPermissionMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.NO_PERMISSION));
        getCommand("channels").setExecutor(new ChannelsCmd());
        getCommand("channels").setPermissionMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.NO_PERMISSION));
        getCommand("kareload").setExecutor(new ReloadConfigCmd(this));
        getCommand("kareload").setPermissionMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.NO_PERMISSION));
        getCommand("clearchat").setExecutor(new ClearChatCmd());
        getCommand("clearchat").setPermissionMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.NO_PERMISSION));
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
                channel.setSetAutoWorld(getConfig().getString("chat.channels." + name + ".autoWorld"));
                channel.setDelete(true);
                if (getConfig().isSet("chat.channels." + name + ".overrideSymbol")) {
                    channel.setOverrideSymbol(getConfig().getString("chat.channels." + name + ".overrideSymbol"));
                } else {
                    channel.setOverrideSymbol("");
                }
                if (getConfig().isSet("chat.channels." + name + ".format")) {
                    channel.setFormat(getConfig().getString("chat.channels." + name + ".format"));
                } else {
                    channel.setFormat(defaultFormat);
                }
                if (getConfig().contains("chat.channels." + name + ".custom")) {
                    for(String key : getConfig().getConfigurationSection("chat.channels." + name + ".custom").getKeys(false)) {
                        channel.getCustom().put(key, getConfig().get("chat.channels." + name + ".custom." + key));
                    }
                }

                if (name.equalsIgnoreCase(defaultChannelCode)) {
                    defaultChannel = channel;
                }

                try {
                    KAChatAPI.getInstance().addChannel(channel);
                } catch (AddChannelException e) {
                    Error("Unable to use channel " + name + " : " + e.getMessage());
                }
            }
        }

        //Atfer reload, if players are using a deleted channel we remove from the list, else, we update in case that
        //the channel has been updated in the config file.
        for(Map.Entry<UUID, Channel> entry : KAChatAPI.getInstance().getPlayersChannel().entrySet()) {
            Channel newChannel = KAChatAPI.getInstance().getChannelFromCommand(entry.getValue().getCommand());
            KAChatAPI.getInstance().getPlayersChannel().remove(entry.getKey());
            Player playerFound = Bukkit.getServer().getPlayer(entry.getKey());

            if (newChannel == null) {
                if (playerFound != null) {
                    Bukkit.getPluginManager().callEvent(new PlayerUpdateChannelEvent(playerFound, entry.getValue(), defaultChannel, PlayerChangeChannelReason.DELETED));
                    playerFound.sendMessage(KAChatAPI.getInstance().getMessageManager().get(MessageManager.CHANNEL_SET_DEFAULT_DELETED));
                }
            } else {
                Bukkit.getPluginManager().callEvent(new PlayerUpdateChannelEvent(playerFound, entry.getValue(), newChannel, PlayerChangeChannelReason.UPDATED));
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
        KAChatAPI.getInstance().getCheckers().add(new GrammarMinSize(getConfig().getInt("security.minSize")));
        KAChatAPI.getInstance().getCheckers().add(new CooldownMessage());
        if (getConfig().getBoolean("security.antiSpam.enable")) {
            KAChatAPI.getInstance().getCheckers().add(new AntiSpam(getConfig().getInt("security.antiSpam.max")));
        }
        KAChatAPI.getInstance().getCheckers().add(new ChatProtect());

        Log("§f" + KAChatAPI.getInstance().getCheckers().size() + " §acheckers registered.");
    }

    private void loadMessageFormatters() {
        KAChatAPI.getInstance().clearMessageFormatters();

        KAChatAPI.getInstance().getMessageFormatters().add(new OverrideSymbolFormatter());
        //First because others formatter can add color
        KAChatAPI.getInstance().getMessageFormatters().add(new ColorFormatter());
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

        Log("§f" + KAChatAPI.getInstance().getMessageFormatters().size() + " §aformatters registered.");
    }

    private void loadChatSaver() {
        stopChatSaver(); //In case that it exist

        if (getConfig().getBoolean("security.save.enable")) {
            ChatSaver chatSaver = new ChatSaver(this, "logs/chat");
            KAChatAPI.getInstance().setChatSaver(chatSaver);
            int internal = getConfig().getInt("security.save.interval") * 20;

            timer = new ChatSaverRunnable(this);
            timer.runTaskTimer(this, internal, internal);
        }
    }

    private void loadTextHover() {
        String mainPath = "playerHover";
        PlayerTextHover playerTextHover = new PlayerTextHover(
                getConfig().getString(mainPath + ".command"),
                getConfig().getStringList(mainPath + ".text")
        );

        KAChatAPI.getInstance().setPlayerTextHover(playerTextHover);
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
        getServer().getConsoleSender().sendMessage(MessageManager.PREFIX + " §f" + message);
    }

    public void Error(String message) {
        Log("§4[§cERROR§4] §c" + message);
    }

    public void Warn(String message) {
        Log("§6[§eWARNING§6] §c" + message);
    }
}
