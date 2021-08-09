package be.kakumi.kachat.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ColorProcessor {
    /**
     * Parses hex colors into supported minecraft colors
     * @param str The color code. The string must start with &# and follow with 6 hex digits describing the color.
     * @return The same color formatted as supported by minecraft game engine.
     */
    public String parseHexFormat(String str) {
        String[] parts = str.split("&#");

        if (parts.length != 2) {
            return str;
        }

        String color = parts[1]; //Get just the 6 hex digits describing the color
        StringBuilder formatted = new StringBuilder("§x"); //&# is always replaced by §x
        formatted.append(
            //Split all 6 values and join them back placing § between them plus one extra at the beginning
            Arrays.stream(color.split(""))
                .collect(Collectors.joining("§", "§", ""))
        );

        return formatted.toString();
    }
}
