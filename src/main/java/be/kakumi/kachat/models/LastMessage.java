package be.kakumi.kachat.models;

import java.util.Calendar;
import java.util.Date;

public class LastMessage {
    private String message;
    private Date date;
    private int counterSameMessage;

    public LastMessage(String message) {
        this.message = message;
        this.date = new Date();
        this.counterSameMessage = 0;
    }

    /***
     * Get the last message sent by the player
     * @return Last message
     */
    public String getMessage() {
        return message;
    }

    /***
     * Get the date of the last message sent by the player
     * @return Date of the last message
     */
    public Date getDate() {
        return date;
    }

    /***
     * Get the number of time the player send the same message.
     * @return Same message counter
     */
    public int getCounterSameMessage() {
        return counterSameMessage;
    }

    /***
     * Update the last sent message
     * @param message Message sent
     */
    public void update(String message) {
        this.message = message;
        this.date = new Date();
    }

    /***
     * Intelligent anti-spam, it makes some checks to know if the message is is quite similar to the last one.
     * If you reach and amount of spam, it will return true, else, even if your message is similar, it will return false.
     * @param message Message sent (formatted)
     * @param max Max number of same message can be send
     * @return True if the message is the same, false if the message is different or counter doesn't reach the max
     */
    public boolean isSameMessage(String message, int max) {
        if (this.message.equalsIgnoreCase(message)) counterSameMessage++;
        //else if (message.length() == this.message.length() && ) counterSameMessage++;
        else if (messageIsSimilarByLength(message)) counterSameMessage++;
        else if (messageIsSimilarByWords(message)) counterSameMessage++;
        else {
            counterSameMessage = 0;
        }

        return counterSameMessage != 0 && counterSameMessage >= max;
    }

    /***
     * Check if you can send the message despite the cooldown
     * @param seconds How many seconds you have to wait between each message
     * @return True if you can send the message
     */
    public boolean canSend(int seconds) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        c1.add(Calendar.SECOND, seconds);
        return c1.compareTo(Calendar.getInstance()) <= 0;
    }

    /***
     * Get how many seconds you still have to wait
     * @param seconds How many seconds you have to wait between each message
     * @return Seconds you still have to wait
     */
    public double getSecondsRemaining(int seconds) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        c1.add(Calendar.SECOND, seconds);
        return ((double) c1.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 1000;
    }

    /***
     * Compare message by length, check if difference after replace is lower than 20%.
     * @param message Message sent
     * @return True if message is similar than the last one.
     */
    private boolean messageIsSimilarByLength(String message) {
        if (this.message.length() - message.length() >= 7) return false;

        int size;
        String wordClear;
        String oldMessage = removePunctuation(this.message);
        String newMessage = removePunctuation(message);
        if (newMessage.length() < oldMessage.length()) {
            size = oldMessage.length();
            wordClear = oldMessage.replace(newMessage, "");
        } else {
            size = newMessage.length();
            wordClear = newMessage.replace(oldMessage, "");
        }

        return ((double) wordClear.length() / size) * 100 <= 20;
    }

    /***
     * Compare message by words, check if the words are similar.
     * It will remove useless characters and then check for each word in the last message and the message sent
     * if word are similar. It calculate how many word are similar divided by amount of word and check if the accuracy
     * is more or equals than 80%.
     * @param message Message sent
     * @return True if message is similar than the last one.
     */
    private boolean messageIsSimilarByWords(String message) {
        String[] wordsSmallerMessage;
        String[] wordsBiggerMessage;

        if (this.message.length() < message.length()) {
            wordsSmallerMessage = this.message.split(" ");
            wordsBiggerMessage = message.split(" ");
        } else {
            wordsSmallerMessage = message.split(" ");
            wordsBiggerMessage = this.message.split(" ");
        }

        //Almost the same number of words
        if (((double) wordsSmallerMessage.length / wordsBiggerMessage.length) * 100 >= 80) {
            int sameWords = 0;
            int emptyWords = 0;
            boolean extraCheck = false; //To know if i + 1 check has been valid one time

            for(int i = 0; i < wordsSmallerMessage.length; i++) { //Salut ça va
                //Explication on the second compareWords :
                //We check if the player just don't put a random word before
                //e.g. : How are you vs Hey how are you
                //It will check How vs Hey : false, then try How vs How : true
                String word1 = removePunctuation(wordsSmallerMessage[i]); //Salut
                if (word1.equals("")) {
                    emptyWords++;
                } else if (i < wordsBiggerMessage.length) {
                    if (compareWords(word1, removePunctuation(wordsBiggerMessage[i]))) {
                        sameWords++;
                    } else if (i + 1 < wordsBiggerMessage.length) {
                        if (compareWords(word1, removePunctuation(wordsBiggerMessage[i + 1]))) {
                            extraCheck = true;
                            sameWords++;
                        }
                    }
                }
            }

            int total = wordsBiggerMessage.length - emptyWords - (extraCheck ? 1 : 0);

            return ((double) sameWords / total) * 100 >= 80;
        }

        return false;
    }

    private boolean compareWords(String word1, String word2) {
        if (word1.equalsIgnoreCase(word2)) {
            return true;
        }

        int firstReplaceLength = word1.replace(word2, "").length();
        int secondReplaceLength = word2.replace(word1, "").length();

        return (firstReplaceLength <= 2 && firstReplaceLength != word1.length()) || (secondReplaceLength <= 2 && secondReplaceLength != word2.length());
    }

    private String removePunctuation(String word) {
        word = word.replaceAll("\\s*?\\.", "");
        word = word.replaceAll("\\s*?:", "");
        word = word.replaceAll("\\s*?\\?", "");
        word = word.replaceAll("\\s*?!", "");
        word = word.replaceAll("\\s*?;", "");
        return word;
    }
}
