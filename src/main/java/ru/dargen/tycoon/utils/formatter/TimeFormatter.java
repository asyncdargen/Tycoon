package ru.dargen.tycoon.utils.formatter;

public class TimeFormatter {

    public static String format(long time) {
        String result = "";
        time /= 1000;
        long temp;
        if ((temp = time / 3600) >=  1) {
            result += String.format(" %s час.", temp);
            time -= temp * 3600;
        }
        if ((temp = time / 60) >=  1) {
            result += String.format(" %s мин.", temp);
            time -= temp * 60;
        }
        if (time >= 1) {
            result += String.format(" %s сек.", time);
        }
        return result.trim();
    }
}
