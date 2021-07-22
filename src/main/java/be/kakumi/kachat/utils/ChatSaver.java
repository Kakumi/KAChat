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

    /***
     * Create the log file
     * @return true if file is created
     * @throws IOException Throw the error if can't create the file
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean createFile() throws IOException {
        if (file.exists()) return true;

        file.getParentFile().mkdirs();
        return file.createNewFile();
    }

    /***
     * Archive the log file by renaming it to : filename-date-number.log
     */
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

    /***
     * Add message into messages list, this list is used to save all the message at the same time every X seconds
     * (Through a BukkitRunnable to avoid read - write - close operation every time a message is sent)
     * @param message Message sent (formatted)
     * @param sent If the message is send in the chat or not
     */
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

    /***
     * Add message into messages list, this list is used to save all the message at the same time every X seconds
     * (Through a BukkitRunnable to avoid read - write - close operation every time a message is sent)
     * @param message Message sent (formatted)
     */
    public void addMessage(String message) {
        addMessage(message, true);
    }

    /***
     * Save all the messages in the log file.
     * @throws IOException Throw the error if can't save in the file
     */
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
