package be.kakumi.kachat.utils;

import be.kakumi.kachat.exceptions.CheckerException;
import be.kakumi.kachat.models.Channel;
import org.bukkit.entity.Player;

public interface Checker {
    /***
     * Check if message is valid, otherwise it must thrown an SecurityException.
     * If this method return true, message will be send, if it return false, message will not be send.
     * It can be useful when you want to execute some action from the message and not show it on the chat.
     * @param player Player who sent the message
     * @param channel Channel used
     * @param message Message sent
     * @return true : valid and message will be send, false : valid but message not send
     * @throws CheckerException Reason why this message is not valid
     */
    boolean valid(Player player, Channel channel, String message) throws CheckerException;

    /***
     * This method is called when you use /kareload, it's useful to know if this checker can be delete and load again
     * It's recommended to set this to false if you use your own checker.
     * It's recommended to set this to true if you load your checker after this plugin initialization
     * @return true: this checker will be delete | false: this checker will not be delete
     */
    boolean delete();
}
