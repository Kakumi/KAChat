package be.kakumi.kachat.utils;

import be.kakumi.kachat.utils.protocollib.WrapperPlayServerChat;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.entity.Player;

import java.util.List;

public class ProtocolLibChatManager extends SimpleChatManager {

    @Override
    public void sendMessage(String message, List<Player> receivers) {
        for(Player p : receivers) {
            WrapperPlayServerChat wrapper = new WrapperPlayServerChat();
            wrapper.setMessage(WrappedChatComponent.fromLegacyText(message));
            wrapper.sendPacket(p);
        }
    }
}
