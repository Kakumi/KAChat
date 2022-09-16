package be.kakumi.kachat.middlewares.security;

import be.kakumi.kachat.exceptions.CheckerException;
import be.kakumi.kachat.exceptions.SecurityBadWordException;
import be.kakumi.kachat.models.Channel;
import be.kakumi.kachat.utils.Checker;
import org.bukkit.entity.Player;

import java.util.List;

public class AntiBadWords implements Checker {
    private List<String> words;
    public AntiBadWords(List<String> words) {
        this.words = words;
    }

    public boolean valid(Player player, Channel channel, String message) throws CheckerException {
        if (player.hasPermission("kachat.bypass.words")) return true;

        for(String word : words) {
            if (message.toLowerCase().contains(word.toLowerCase())) {
                throw new SecurityBadWordException(word);
            }
        }

        return true;
    }

    public boolean delete() {
        return true;
    }
}
