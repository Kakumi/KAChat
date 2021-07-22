package be.kakumi.kachat.utils;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatSaver {
    private final List<String> messages;
    private final String filename;
    private final File file;

    public ChatSaver(JavaPlugin main, String filename) {
        this.messages = new ArrayList<>();
        this.filename = filename;
        this.file = new File(main.getDataFolder() + "/" + filename + ".log");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean createFile() throws IOException {
        if (file.exists()) return true;

        file.getParentFile().mkdirs();
        return file.createNewFile();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void rename() {
        if (file.exists()) {
            Calendar date = Calendar.getInstance();
            String dateText = date.get(Calendar.DAY_OF_MONTH) + "-" + (date.get(Calendar.MONTH) + 1) + "-" + date.get(Calendar.YEAR);

            int fileCounter = 1;
            File newFile;
            do {
                newFile = new File(file.getPath().replaceAll("\\.\\w+", "") + "-" + dateText + "-" + fileCounter + ".log");
                fileCounter++;
            } while (newFile.exists());

            file.renameTo(newFile);
        }
    }

    public void addMessage(String message, boolean sent) {
        Calendar date = Calendar.getInstance();
        StringBuilder tags = new StringBuilder();
        tags.append("[");
        tags.append(date.get(Calendar.DAY_OF_MONTH));
        tags.append("/");
        tags.append(date.get(Calendar.MONTH) + 1);
        tags.append("/");
        tags.append(date.get(Calendar.YEAR));
        tags.append(" ");
        tags.append(date.get(Calendar.HOUR));
        tags.append(":");
        tags.append(date.get(Calendar.MINUTE));
        tags.append(":");
        tags.append(date.get(Calendar.SECOND));
        tags.append("]");
        if (!sent) {
            tags.append("[CANCELLED]");
        }
        message = message.replaceAll("§.", "");

        messages.add(tags + " " + message);
    }

    public void addMessage(String message) {
        addMessage(message, true);
    }

    public void saveMessages() throws IOException {
        if (messages.size() > 0) {
            FileOutputStream fos = new FileOutputStream(file, true);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            for(String message : messages) {
                bw.write(message);
                bw.newLine();
            }

            bw.flush();
            bw.close();

            messages.clear();
        }
    }
}
