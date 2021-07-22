package be.kakumi.kachat.timers;

import be.kakumi.kachat.KAChat;
import be.kakumi.kachat.api.KAChatAPI;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

public class ChatSaverRunnable extends BukkitRunnable {
    private final KAChat main;
    public ChatSaverRunnable(KAChat main) {
        this.main = main;
    }

    public void run() {
        try {
            main.Log("Start saving the chat...");
            if (KAChatAPI.getInstance().getChatSaver() != null) {
                if (KAChatAPI.getInstance().getChatSaver().createFile()) {
                    KAChatAPI.getInstance().getChatSaver().saveMessages();
                    main.Log("Save completed!");
                } else {
                    main.Error("An error occured during the save because the file does not longer exist.");
                }
            }
        } catch (IOException e) {
            main.Error("Can't save the chat !");
            e.printStackTrace();
        }
    }
}
