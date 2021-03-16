package ru.dargen.tycoon.utils.formatter;

public class PrestigeFormatter {

    private static final int[] PRESTIGES = {350, 275, 200, 150, 110, 85, 60, 35, 20, 10};
    private static final String[] COLORS = {"4", "c", "5", "d", "3", "b", "6", "e", "2", "a"};

    public static String format(int prestige) {
        if (prestige <= 0)
            return "§cНет";
        int num = 0;
        for (int i = PRESTIGES.length - 1; i >= 0; i--) {
            if (prestige > PRESTIGES[i])
                continue;
            num = i;
            break;
        }
        return "§" + COLORS[num] + RomanNumbersFormatter.format(prestige);
    }

}
