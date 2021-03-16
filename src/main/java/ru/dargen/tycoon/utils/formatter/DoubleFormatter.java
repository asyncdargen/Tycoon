package ru.dargen.tycoon.utils.formatter;

import java.text.DecimalFormat;

public class DoubleFormatter {

    private static final DecimalFormat format = new DecimalFormat("##.##");
    private static final String[] formats = new String[34];

    static {
        formats[33] = "D";
        formats[30] = "N";
        formats[27] = "0";
        formats[24] = "S";
        formats[21] = "s";
        formats[18] = "Q";
        formats[15] = "q";
        formats[12] = "T";
        formats[9] = "B";
        formats[6] = "M";
        formats[3] = "K";
    }

    public static String format(double toFormat) {
        String formatted = "";
        for (int i = 33; i > 3; i -= 3) {
            double timely = toFormat / Math.pow(10, i);
            if (timely > 1d) {
                formatted = format.format(timely) + formats[i];
                break;
            }
        }
        if (formatted.isEmpty())
            formatted = format.format(toFormat);
        while (formatted.contains(".") && (formatted.endsWith("0") || formatted.endsWith(".")))
            formatted = formatted.substring(0, formatted.length() - 1);
        return formatted;
    }
}
