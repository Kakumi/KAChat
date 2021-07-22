package be.kakumi.kachat.middlewares.security;

import be.kakumi.kachat.exceptions.CheckerException;
import be.kakumi.kachat.exceptions.SecurityProtectException;
import be.kakumi.kachat.utils.Checker;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class ChatProtect implements Checker {
    public boolean valid(Player player, String message) throws CheckerException {
        if (Pattern.matches("^[\\w|\\d]{10,}$", message)) {
            throw new SecurityProtectException();
        }

        return true;
    }

    public boolean delete() {
        return true;
    }
}
