package be.kakumi.kachat.models;

import java.util.List;

public class PlayerTextHover {
    private String command;
    private List<String> lines;

    public PlayerTextHover(String command, List<String> lines) {
        this.command = command;
        this.lines = lines;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }
}
