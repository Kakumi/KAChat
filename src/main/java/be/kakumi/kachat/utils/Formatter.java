package be.kakumi.kachat.utils;

import org.bukkit.entity.Player;

public interface Formatter {
    String format(Player player, String message);

    /***
     * This method is called when you use /kareload, it's useful to know if this checker can be delete and load again
     * It's recommended to set this to false if you use your own checker.
     * It's recommended to set this to true if you load your checker after this plugin initialization
     * @return true: this checker will be delete | false: this checker will not be delete
     */
    boolean delete();
}
