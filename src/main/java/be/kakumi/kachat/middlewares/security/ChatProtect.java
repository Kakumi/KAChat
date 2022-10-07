package be.kakumi.kachat.middlewares.security;

import be.kakumi.kachat.exceptions.CheckerException;
import be.kakumi.kachat.exceptions.SecurityProtectException;
import be.kakumi.kachat.models.Channel;
import be.kakumi.kachat.utils.Checker;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class ChatProtect implements Checker {
    public boolean valid(Player player, Channel channel, String message) throws CheckerException {
        if (Pattern.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&._-]{8,}$", message)) {
            throw new SecurityProtectException();
        }

        return true;
    }

    public boolean delete() {
        return true;
    }
}
