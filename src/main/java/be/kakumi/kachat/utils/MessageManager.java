package be.kakumi.kachat.utils;

import be.kakumi.kachat.KAChat;
import be.kakumi.kachat.exceptions.MessagesFileException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class MessageManager {
    public static final String PREFIX = "§9[§bKAChat§9]";
    public static final String NO_PERMISSION = "no_permission";
    public static final String NO_PERMISSION_FORCE_TAGS = "no_permission_force_tag";
    public static final String MUST_BE_A_PLAYER = "must_be_a_player";
    public static final String RELOAD_CONFIG = "reload_config";
    public static final String CLEAR_CHAT = "clear_chat";
    public static final String CLEAR_CHAT_ALL = "clear_chat_all";
    public static final String ERROR_OCCURRED = "error_occurred";
    public static final String INVALID_NUMBER_ARG = "invalid_number_arg";
    public static final String YES = "yes_text";
    public static final String NO = "no_text";
    public static final String UNLIMITED = "unlimited";
    public static final String CHANNEL_SET_DEFAULT = "channel.set_default";
    public static final String CHANNEL_SET_DEFAULT_DELETED = "channel.set_default_deleted";
    public static final String CHANNEL_NOT_CONNECTED = "channel.not_connected";
    public static final String CHANNEL_NO_PERMISSION_SET = "channel.no_permission_set";
    public static final String CHANNEL_DOESNT_EXIST = "channel.doesnt_exist";
    public static final String CHANNEL_SET_MYSELF = "channel.set_myself";
    public static final String CHANNEL_SET_PLAYER = "channel.set_player";
    public static final String CHANNEL_WRONG_WORLD_MYSELF = "channel.wrong_world_myself";
    public static final String CHANNEL_WRONG_WORLD_PLAYER = "channel.wrong_world_player";
    public static final String CHANNEL_NO_PERMISSION_USE_MYSELF = "channel.no_permission_use_myself";
    public static final String CHANNEL_NO_PERMISSION_USE_PLAYER = "channel.no_permission_use_player";
    public static final String CHANNEL_WORLD_RESTRICTED = "channel.world_restricted";
    public static final String CHANNEL_AUTO_WORLD = "channel.auto_word";
    public static final String SECURITY_MIN_SIZE = "security.min_size";
    public static final String SECURITY_ADS = "security.ads";
    public static final String SECURITY_BAD_WORD = "security.bad_word";
    public static final String SECURITY_CAPSLOCK = "security.capslock";
    public static final String SECURITY_COOLDOWN = "security.cooldown";
    public static final String SECURITY_STRANGE = "security.strange";
    public static final String SECURITY_SPAM = "security.spam";
    public static final String INVENTORY_PREVIOUS_PAGE = "inventory.previous_page";
    public static final String INVENTORY_NEXT_PAGE = "inventory.next_page";
    public static final String INVENTORY_CHANNELS_TITLE = "inventory.channels.title";
    public static final String INVENTORY_CHANNELS_LORE_PREFIX = "inventory.channels.lore_prefix";
    public static final String INVENTORY_CHANNELS_LORE_RANGE = "inventory.channels.lore_range";
    public static final String INVENTORY_CHANNELS_LORE_PRIVATE = "inventory.channels.lore_private";
    public static final String INVENTORY_CHANNELS_LORE_WORLD = "inventory.channels.lore_world";
    public static final String INVENTORY_CHANNELS_LORE_SYMBOL = "inventory.channels.lore_symbol";

    private final YamlConfiguration messages;
    private final YamlConfiguration template;
    private final boolean usePrefix;

    public MessageManager(KAChat main) throws MessagesFileException {
        usePrefix = main.getConfig().getBoolean("usePrefix");

        main.saveResource("messages/en.yml", false);
        main.saveResource("messages/fr.yml", false);
        main.saveResource("messages/template.yml", true);
        String codeFile = main.getConfig().getString("lang");
        //Load user language file
        File file = new File(main.getDataFolder() + "/messages/" + codeFile + ".yml");
        if (file.exists()) {
            messages = YamlConfiguration.loadConfiguration(file);
        } else {
            throw new MessagesFileException();
        }

        //Load template file
        File fileTemplate = new File(main.getDataFolder() + "/messages/template.yml");
        if (fileTemplate.exists()) {
            template = YamlConfiguration.loadConfiguration(fileTemplate);

            //Generate missing values
            for(String node : template.getConfigurationSection("").getKeys(true)) {
                if (!messages.isSet(node)) {
                    messages.set(node, template.get(node));
                }
            }

            try {
                messages.save(file);
            } catch (IOException e) {
                throw new MessagesFileException();
            }
        } else {
            throw new MessagesFileException();
        }
    }

    @SuppressWarnings("ConstantConditions")
    public String get(@NotNull String path, @NotNull List<String> params) {
        StringBuilder message = new StringBuilder();
        if (usePrefix) {
            message.append(PREFIX).append(" §r");
        }

        if (messages.isSet(path)) {
            String messageFile = messages.getString(path).replace("&", "§");
            for(int i = 0; i < params.size(); i++) {
                messageFile = messageFile.replace("$" + (i + 1), params.get(i));
            }
            message.append(messageFile);

            return message.toString();
        }

        message.append("message_not_found");

        return message.toString();
    }

    public String get(String path) {
        return get(path, Collections.emptyList());
    }
}
