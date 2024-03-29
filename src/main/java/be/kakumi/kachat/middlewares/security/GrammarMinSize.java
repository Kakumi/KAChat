package be.kakumi.kachat.middlewares.security;

import be.kakumi.kachat.exceptions.CheckerException;
import be.kakumi.kachat.exceptions.SecurityMinSizeException;
import be.kakumi.kachat.models.Channel;
import be.kakumi.kachat.utils.Checker;
import org.bukkit.entity.Player;

public class GrammarMinSize implements Checker {
    private int size;
    public GrammarMinSize(int size) {
        this.size = size;
    }

    public boolean valid(Player player, Channel channel, String message) throws CheckerException {
        if (player.hasPermission("kachat.bypass.minsize")) return true;

        if (message.length() < size) {
            throw new SecurityMinSizeException();
        }

        return true;
    }

    public boolean delete() {
        return true;
    }
}
