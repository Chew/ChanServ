package pw.chew.chanserv.util;

import java.awt.Color;

public class MiscUtil {
    public static double getColorDistance(Color a, Color b) {
        return Math.sqrt(Math.pow(a.getRed() - b.getRed(), 2) + Math.pow(a.getGreen() - b.getGreen(), 2) + Math.pow(a.getBlue() - b.getBlue(), 2));
    }

    public static float colorSimilarityPercentage(Color a, Color b) {
        float per = (float) (100 - (getColorDistance(a, b) / 441.6729559300637) * 100);
        return per / 100;
    }
}
