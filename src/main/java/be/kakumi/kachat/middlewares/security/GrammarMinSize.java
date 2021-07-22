package be.kakumi.kachat.middlewares.security;

import be.kakumi.kachat.exceptions.CheckerException;
import be.kakumi.kachat.exceptions.GrammarMinSizeException;
import be.kakumi.kachat.utils.Checker;
import org.bukkit.entity.Player;

public class GrammarMinSize implements Checker {
    private int size;
    public GrammarMinSize(int size) {
        this.size = size;
    }

    public boolean valid(Player player, String message) throws CheckerException {
        if (player.hasPermission("kachat.bypass.minsize")) return true;

        if (message.length() < size) {
            throw new GrammarMinSizeException();
        }

        return true;
    }

    public boolean delete() {
        return true;
    }
}
