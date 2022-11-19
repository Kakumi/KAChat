package be.kakumi.kachat.middlewares.message;

import be.kakumi.kachat.api.KAChatAPI;
import be.kakumi.kachat.utils.Formatter;
import org.bukkit.entity.Player;

import java.util.Map;

public class ReplacersFormatter implements Formatter {
    @Override
    public String format(Player player, String message) {
        for(Map.Entry<String, String> entry : KAChatAPI.getInstance().getMessageReplacers().entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue());
        }

        return message;
    }

    @Override
    public boolean delete() {
        return true;
    }
}
