package com.JIRSendApp.view.cli;

import java.io.IOException;

public class CliTools {
    public static final boolean isWindowsSystem = System.getProperty("os.name").contains("Windows");

    private static final boolean altLogo = false;
    private static final String textLogo = " --------------------------- \n" +
            "-----------------------------\n" +
            "-----------------------------\n" +
            "-----------------------------\n" +
            "-----------=@@@--+@@@@@@+----\n" +
            "-----------=@@@-=@@@=+%%=----\n" +
            "-----------=@@@--@@@@*-------\n" +
            "-----------=@@@---*@@@@@%----\n" +
            "-----------=@@@------=%@@%---\n" +
            "------+@@@=%@@@-@@@@==@@@%---\n" +
            "-------+@@@@@#---#@@@@@@#----\n" +
            " --------------------------- ";

    private static final String textLogoAlt = " --------------------------- \n" +
            "-----------------------------\n" +
            "-----------------------------\n" +
            "-----------------------------\n" +
            "-----------------------------\n" +
            "-----------------------------\n" +
            "-----------=@@@--+@@@@@@+----\n" +
            "-----------=@@@-=@@@=+%%=----\n" +
            "-----------=@@@--@@@@*-------\n" +
            "-----------=@@@---*@@@@@%----\n" +
            "-----------=@@@------=%@@%---\n" +
            "------+@@@=%@@@-@@@@==@@@%---\n" +
            "-------+@@@@@#---#@@@@@@#----\n" +
            "-----------------------------\n" +
            " --------------------------- ";

    // ANSI escape codes
    public static final String NORMAL = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String THIN = "\u001B[2m";
    public static final String ITALIC = "\u001B[3m";
    public static final String UNDERLINED = "\u001B[4m";
    public static final String BLINK = "\u001B[5m";
    public static final String BLINK2 = "\u001B[6m";
    public static final String REVERSED = "\u001B[7m";
    public static final String INVISIBLE = "\u001B[8m";
    public static final String STRIKED = "\u001B[9m";

    public static final String BLACK_NORMAL_COLOR = "\u001B[30m";
    public static final String RED_NORMAL_COLOR = "\u001B[31m";
    public static final String GREEN_NORMAL_COLOR = "\u001B[32m";
    public static final String YELLOW_NORMAL_COLOR = "\u001B[33m";
    public static final String BLUE_NORMAL_COLOR = "\u001B[34m";
    public static final String PURPLE_NORMAL_COLOR = "\u001B[35m";
    public static final String CYAN_NORMAL_COLOR = "\u001B[36m";
    public static final String WHITE_NORMAL_COLOR = "\u001B[37m";
    public static final String BLACK_NORMAL_BACKGROUND = "\u001B[40m";
    public static final String RED_NORMAL_BACKGROUND = "\u001B[41m";
    public static final String GREEN_NORMAL_BACKGROUND = "\u001B[42m";
    public static final String YELLOW_NORMAL_BACKGROUND = "\u001B[43m";
    public static final String BLUE_NORMAL_BACKGROUND = "\u001B[44m";
    public static final String PURPLE_NORMAL_BACKGROUND = "\u001B[45m";
    public static final String CYAN_NORMAL_BACKGROUND = "\u001B[46m";
    public static final String WHITE_NORMAL_BACKGROUND = "\u001B[47m";
    public static final String BLACK_DESAT_COLOR = "\u001B[90m";
    public static final String RED_DESAT_COLOR = "\u001B[91m";
    public static final String GREEN_DESAT_COLOR = "\u001B[92m";
    public static final String YELLOW_DESAT_COLOR = "\u001B[93m";
    public static final String BLUE_DESAT_COLOR = "\u001B[94m";
    public static final String PURPLE_DESAT_COLOR = "\u001B[95m";
    public static final String CYAN_DESAT_COLOR = "\u001B[96m";
    public static final String WHITE_DESAT_COLOR = "\u001B[97m";
    public static final String BLACK_DESAT_BACKGROUND = "\u001B[100m";
    public static final String RED_DESAT_BACKGROUND = "\u001B[101m";
    public static final String GREEN_DESAT_BACKGROUND = "\u001B[102m";
    public static final String YELLOW_DESAT_BACKGROUND = "\u001B[103m";
    public static final String BLUE_DESAT_BACKGROUND = "\u001B[104m";
    public static final String PURPLE_DESAT_BACKGROUND = "\u001B[105m";
    public static final String CYAN_DESAT_BACKGROUND = "\u001B[106m";
    public static final String WHITE_DESAT_BACKGROUND = "\u001B[107m";

    public static void clearConsoleANSI() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Automatically colorize text whether it is windows or not
     * 
     * @param color
     * @return color or not
     */
    public static String colorize(String color) {
        return isWindowsSystem ? "" : color;
    }

    /**
     * Automatically colorize text whether it is windows or not
     * 
     * @param color
     * @return colored text
     */
    public static String colorize(String color, String textToColor) {
        return isWindowsSystem ? textToColor : color + textToColor + NORMAL;
    }

    /**
     * Automatically prints colorized text whether it is windows or not
     * 
     * @param color
     */
    public static void coloredPrintln(String color, String textToColor) {
        System.out.println(colorize(color, textToColor));
    }

    /**
     * Automatically prints colorized text whether it is windows or not
     * 
     * @param color
     */
    public static void coloredPrint(String color, String textToColor) {
        System.out.print(colorize(color, textToColor));
    }

    public static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033\143");
            }
        } catch (IOException | InterruptedException ex) {
        }
    }

    public static void printLogo(boolean color) {
        if (!color) {
            System.out.println((altLogo ? textLogoAlt : textLogo) + "\n");
            return;
        }
        StringBuilder coloredTextLogo = new StringBuilder();
        boolean isPink = false;
        for (char c : (altLogo ? textLogoAlt : textLogo).toCharArray()) {
            if (c == '\n' || c == ' ') {
                coloredTextLogo.append(c);
                continue;
            } else if (c == '-' && !isPink) {
                coloredTextLogo.append(PURPLE_NORMAL_COLOR);
                isPink = true;
            } else if (c != '-' && isPink) {
                coloredTextLogo.append(WHITE_NORMAL_COLOR);
                isPink = false;
            }
            coloredTextLogo.append(c);
        }
        coloredTextLogo.append(NORMAL).append('\n');
        System.out.println(coloredTextLogo);
    }

    public static void printLogo() {
        printLogo(!isWindowsSystem);
    }

    public static void printTitle(boolean color, boolean authors) {
        System.out.println("*****************************");
        if (color)
            System.out.print(PURPLE_NORMAL_COLOR + BOLD + "JIR" + WHITE_NORMAL_COLOR + "Send" + NORMAL);
        else
            System.out.print("JIRSend");
        System.out.println(" - Local ChatSystem");

        if (authors) {
            System.out.println(colorize(BLACK_DESAT_COLOR) + "    - by: Victor LASSERRE");
            System.out.println("          Valentin SERVIERES" + colorize(NORMAL));
        }

        System.out.println("*****************************\n");
    }

    public static void printTitle(boolean authors) {
        printTitle(!isWindowsSystem, authors);
    }

    public static void printBigError(String message) {
        System.out
                .println(CliTools.colorize(CliTools.RED_NORMAL_BACKGROUND + CliTools.WHITE_NORMAL_COLOR + CliTools.BOLD)
                        + message + CliTools.colorize(CliTools.NORMAL));
    }
}
