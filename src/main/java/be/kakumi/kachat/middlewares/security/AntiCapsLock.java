package be.kakumi.kachat.middlewares.security;

import be.kakumi.kachat.exceptions.CheckerException;
import be.kakumi.kachat.exceptions.SecurityCapsLockException;
import be.kakumi.kachat.utils.Checker;
import org.bukkit.entity.Player;

public class AntiCapsLock implements Checker {
    private int max;
    public AntiCapsLock(int max) {
        this.max = max;
    }

    public boolean valid(Player player, String message) throws CheckerException {
        if (player.hasPermission("kachat.bypass.capslock")) return true;

        int numberOfUppercase = 0;
        int numberOfCharacters = 0;
        for(char c : message.toCharArray()) {
            if (Character.isUpperCase(c)) numberOfUppercase++;
            if (Character.isLetter(c)) numberOfCharacters++;
        }

        if (Character.isUpperCase(message.toCharArray()[0])) {
            numberOfUppercase--; //Because first letter is in uppercase (Grammar)
        }

        //We remove space because it's not a character
        if (((double) numberOfUppercase / numberOfCharacters) * 100 >= max) {
            throw new SecurityCapsLockException();
        }

        return true;
    }

    public boolean delete() {
        return true;
    }
}
