# KAChat
This plugin is a chat manager for minecraft. Avaible from spigot 1.7.10 to 1.16.5 (tested).
<br>You can create a channel and configure them quite easily to be able to create a lot of chats with permission, world restriction and range. Also, this plugin provides some security checker and formatter like [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/).
<br>You can use our API to create custom channels and be able to create your behaviour for every chat. You can almost change everything, and it was the goal of this plugin.
<br>**Actually, this plugin is in version 1.0 so feel free to suggest some ideas and report bugs.**
## Commands
- **/kareload**: Reload the config file
- **/channel [channel] [player] [-fp (force permission)] [-fw (force world)] [-f (force all)]**: Change the channel of someone, or you by default
- **/channels**: Open a GUI with all listable channels
- **/clearchat [-a (everyone)]**: Clear the chat

## Permissions
- **kachat.bypass.***: Allow the player to bypass all security
- **kachat.bypass.color**: Allow the player to use color and format in chat
- **kachat.bypass.capslock**: Bypass anti-capslock system
- **kachat.bypass.spam**: Bypass anti-spam system
- **kachat.bypass.ads**: Bypass anti-ads system
- **kachat.bypass.words**: Bypass blocked words system
- **kachat.bypass.cooldown**: Bypass cooldown system
- **kachat.bypass.minsize**: Bypass message min size
- **kachat.cmd.channel**: Allow player to change channel through /channel
- **kachat.cmd.channel.others**: Change the channel of a player
- **kachat.cmd.channel.force**: Allow the player to force use channel even if he doesn't have the permission
- **kachat.cmd.channels**: Allow player to change channel through /channels
- **kachat.cmd.reload**: Allow player to reload config file and load changes
- **kachat.cmd.clearchat**: Allow player to clear the chat
- **kachat.cmd.clearchat.all**: Allow player to clear for everyone

## Middleware
### Checkers
Checkers are used to be sure the message respect some restrictions otherwise the message will be blocked. These are the one offer by the plugin. They can be disabled in the config file.
1. **Anti-advertisement**: Blocks URLs and servers IP
1. **Bad words**:  Blocks words that are listed as bad
1. **Anti-capslock**:  Blocks messages with XX% capitalize letters
1. **Anti-spam**: Blocks repetitive messages (this functionalities can be a bit buggy because it makes some checks between each word, it is quite intelligent but need more tests to be sure it's working well)
1. **Chat protect**: Blocks messages that are strange to send (to avoid bot spam)
1. **Cooldown Message**: Blocks messages if there is a cooldown in the channel
1. **Min Size**: Blocks messages if the size is lower than the one set in the config file

### Formatters
Formatters are used to make changes on the sent message.
1. **Capitalize**: capitalize the first letter of the message
1. **Auto dot**: Add a dot at the end of the sentence
1. **Mention**: Mention player's in the chat and play sound
1. **Color**: Allow players to use color and format in the chat (if they have the permission)

### Placeholders
Placeholders are used to replace some text in the message by some plugins values.
1. **KAChatAPI**:
    * {message}: The message sent by the player (real message)
    * {channel}: The player's channel prefix
    * {color}: The player's color (white or red if OP)
    * {chat_color}: The player's channel color
    * {player}: The player's name
    * _More will be added soon._
2. **PlaceholderAPI**:
    * [List of placeholders](https://github.com/PlaceholderAPI/PlaceholderAPI/wiki/Placeholders)
    * Be sure to download the extension you need by doing the command : /papi ecloud download <Extension>
3. **Vault**:
    * {vault_eco_name}: Name of the economy plugin
    * {vault_eco_money}: Balance of the player
    * {vault_eco_money.2f}: Balance of the player with 2 decimals
    * {vault_eco_currency_name}: Name of money
    * {vault_eco_currency_name_plural}: Name of money in plural
    * {vault_chat_name}: Name of the chat plugin
    * {vault_chat_player_prefix}: Player's primary group prefix
    * {vault_chat_player_suffix}: Player's primary group suffix
    * {vault_chat_player_primary_group}: Player's primary group
    * {vault_perm_group}: Name of the permission plugin
## API
### How to use the API
First you need to install the dependency through .jar file, Maven or Gradle.
<br>**Replace \<release version> with the desired version**
<br>Maven :
```xml
<project>
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>com.github.Kakumi</groupId>
            <artifactId>KAChat</artifactId>
            <version><release version></version>
        </dependency>
    </dependencies>
</project>
```
<br>Gradle:
```
    allprojects {
        repositories {
            maven { url 'https://jitpack.io' }
        }
    }

    dependencies {
        implementation 'com.github.Kakumi:KAChat:<release version>'
    }
```

Then, you can use the plugin:
```java
public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("KAChat") != null) {
            //Access the API through KAChatAPI.getInstance()
            KAChatAPI.getInstance().getChannels().size();
        } else {
            //Plugin doesn't exist
            getPluginLoader().disablePlugin(this);
        }
    }
}
```
### What can you do ?
- Create custom channels and register them
- Change how to select players available to receive the message
- Change how to send the message
- Change the behaviour to select the player's color
- Change the behaviour to select the chat color
- Change how to save messages in the log file (disabled by default but working)
- Add or remove checkers, formatters and placeholders
- Retrieve information such as player's channel, player's last message, default format, default channel, ...

All methods are documented so know how to use then.

### Examples
#### New Channel
```java
public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        Channel channel = new Channel("§7[§bJail§7]", "jail");
        channel.setListed(false);
        channel.setColor("§8");
        channel.setCooldown(0);
        channel.setForInside(true);
        channel.setWorld("");
        channel.setRange(0);
        channel.setPermissionToUse("");
        channel.setPermissionToSee("");
        channel.setSetAutoWorld("");
        channel.setDelete(false);
        channel.setOverrideSymbol("^");
        channel.setFormat(KAChatAPI.getInstance().getDefaultFormat());

        //Not necessary, you can store channel in your list and retrieve it by adding behaviour to ChatManager.
        try {
            KAChatAPI.getInstance().addChannel(channel);
        } catch (AddChannelException e) {
            e.printStackTrace();
        }
    }
}
```

#### Checker
```java
public class MyChecker implements Checker {
    public boolean valid(Player player, String message) throws CheckerException {
        if (message.contains("test")) throw new SecurityTestException();
        return true;
    }

    //Should be false for you, if true, this checker will be removed after KAChat reload.
    public boolean delete() {
        return false;
    }
}

public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        KAChatAPI.getInstance().getCheckers().add(new MyChecker());
    }
}
```

#### Message formatter
```java
public class MyFormatter implements Formatter {
    public String format(Player player, String message) {
        return message.replace("m", "k");
    }

    //Should be false for you, if true, this formatter will be removed after KAChat reload.
    public boolean delete() {
        return false;
    }
}

public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        KAChatAPI.getInstance().getMessageFormatters().add(new MyFormatter());
    }
}
```

#### Placeholder
```java
public class MyPlaceholder implements Placeholder {
    public String format(Player player, Channel channel, String message) {
        return message.replace("{placeholder}", player.getName());
    }
}

public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        KAChatAPI.getInstance().getPlaceholders().add(new MyPlaceholder());
    }
}
```

#### ChatManager
```java
public class CustomChatManager extends ChatManager {
    @Override
    public List<Player> getNearbyPlayers(Channel channel, Player player) {
        List<Player> nearbyPlayers = new ArrayList<>();
        if (channel.getChannelType() == MyEnum.TEST) {
            nearbyPlayers.add(player);
            Player anotherPlayer = Bukkit.getServer().getPlayerExact("test");
            if (anotherPlayer != null) {
                nearbyPlayers.add(anotherPlayer);   
            }
        } else {
            return super.getNearbyPlayers(channel, player);
        }

        return nearbyPlayers;
    }

    @Override
    public String getPlayerColor(Player player) {
        if (player.hasPermission("op")) {
            return "&b";
        }

        return super.getPlayerColor(player);
    }
}

public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        KAChatAPI.getInstance().setChatManager(new CustomChatManager());
    }
}
```

#### Custom Events
```java
public class CustomListener implements Listener {
    @EventHandler
    public void onChannelReceiveMessage(ChannelReceiveMessageEvent event) {
        
    }

    @EventHandler
    public void onChannelReceiveMessage(PlayerUpdateChannelEvent event) {
        
    }
}
```