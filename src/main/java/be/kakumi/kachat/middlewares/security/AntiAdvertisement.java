package be.kakumi.kachat.middlewares.security;

import be.kakumi.kachat.exceptions.CheckerException;
import be.kakumi.kachat.exceptions.SecurityAdsException;
import be.kakumi.kachat.utils.Checker;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class AntiAdvertisement implements Checker {
    public boolean valid(Player player, String message) throws CheckerException {
        if (player.hasPermission("kachat.bypass.ads")) return true;

        String url = "(https?://)?(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)";
        String ip = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}(:\\d{5})?";
        if (Pattern.matches(url, message) || Pattern.matches(ip, message)) {
            throw new SecurityAdsException();
        }

        return true;
    }

    public boolean delete() {
        return true;
    }
}
