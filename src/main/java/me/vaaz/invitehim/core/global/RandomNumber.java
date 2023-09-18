package me.vaaz.invitehim.core.global;

public class RandomNumber {

    public static int randomInteger(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    public static double randomDouble(double min, double max) {
        return min + (Math.random() * ((max - min) + 1));
    }

    public static float randomFloat(float min, float max) {
        return min + (float) (Math.random() * ((max - min) + 1));
    }

}