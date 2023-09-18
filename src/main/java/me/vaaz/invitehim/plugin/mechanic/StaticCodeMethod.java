package me.vaaz.invitehim.plugin.mechanic;

import me.vaaz.invitehim.core.global.RandomNumber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class StaticCodeMethod {

    public static ArrayList<Character> getAllowedChars() {
        ArrayList<Character> allowedChars = new ArrayList<>();

        // Lower case letters
        allowedChars.addAll(java.util.List.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'));
        // Uppercase letters
        allowedChars.addAll(java.util.List.of('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'));
        // Numbers
        allowedChars.addAll(java.util.List.of('1', '2', '3', '4', '5', '6', '7', '8', '9', '0'));
        // Some symbols
        allowedChars.addAll(java.util.List.of('_', '!'));

        return allowedChars;
    }

    public static String generateRandomKey() {
        String prefix = "CODE_"; // The random code prefix, all codes will start with it
        int max_length = 16 - prefix.length(); // Replace 16 with the max length of code

        // Generate the code and add it to a StringBuilder
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < max_length; i++) {
            key.append(getAllowedChars().get(RandomNumber.randomInteger(0, getAllowedChars().size() - 1)));
        }

        // Return complete generated code
        return prefix + key.toString();
    }

}
