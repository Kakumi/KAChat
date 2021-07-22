package be.kakumi.kachat.middlewares.message;

import be.kakumi.kachat.utils.Formatter;
import org.bukkit.entity.Player;

public class GrammarDot implements Formatter {
    public String format(Player player, String message) {
        char[] messageInChar = message.toCharArray();
        if (!isPunctuationSymbol(messageInChar[messageInChar.length - 1])) {
            return message + ".";
        }

        return message;
    }

    private boolean isPunctuationSymbol(char c) {
        switch (c) {
            case '.':
            case '?':
            case '!':
            case ';':
            case ':': return true;
            default: return false;
        }
    }

    public boolean delete() {
        return true;
    }
}
