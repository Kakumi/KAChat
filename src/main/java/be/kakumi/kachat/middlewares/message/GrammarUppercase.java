package be.kakumi.kachat.middlewares.message;

import be.kakumi.kachat.utils.Formatter;
import org.bukkit.entity.Player;

public class GrammarUppercase implements Formatter {
    public String format(Player player, String message) {
        char[] messageInChar = message.toCharArray();
        if (Character.isLowerCase(messageInChar[0])) {
            messageInChar[0] = Character.toUpperCase(messageInChar[0]);
        }

        return String.valueOf(messageInChar);
    }

    public boolean delete() {
        return true;
    }
}
