package be.kakumi.kachat.middlewares.message;

import be.kakumi.kachat.utils.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class MentionPlayer implements Formatter {
    private final String symbolToUse;
    private final String symbolToSee;
    private final String color;
    private final boolean playSound;

    public MentionPlayer(String symbolToUse, String symbolToSee, String color, boolean playSound) {
        this.symbolToUse = symbolToUse;
        this.symbolToSee = symbolToSee;
        this.color = color;
        this.playSound = playSound;
    }

    public String format(Player player, String message) {
        for(Player playerOnline : Bukkit.getServer().getOnlinePlayers()) {
            if (message.contains(symbolToUse + playerOnline.getName())) {
                message = message.replace(symbolToUse + playerOnline.getName(), color + symbolToSee + playerOnline.getName() + "§f");

                if (playSound) {
                    playerOnline.playSound(playerOnline.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 1f);
                }
            }
        }

        return message;
    }

    public boolean delete() {
        return true;
    }
}
