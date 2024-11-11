package com.JIRSend.view.cli;

import java.io.IOException;

public class CliTools {
    public static final boolean isWindowsSystem = System.getProperty("os.name").contains("Windows");

    private static final boolean altLogo = false;
    private static final String textLogo = " --------------------------- \n" +
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
    public static final String PINK = "\u001B[35m";
    public static final String WHITE = "\u001B[37m";
    public static final String RESET = "\u001B[0m"; // reset to default console color

    public static void clearConsoleANSI() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
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
            System.out.println((altLogo ? textLogo : textLogoAlt) + "\n");
            return;
        }
        StringBuilder coloredTextLogo = new StringBuilder();
        boolean isPink = false;
        for (char c : (altLogo ? textLogo : textLogoAlt).toCharArray()) {
            if (c == '\n' || c == ' ') {
                coloredTextLogo.append(c);
                continue;
            } else if (c == '-' && !isPink) {
                coloredTextLogo.append(PINK);
                isPink = true;
            } else if (c != '-' && isPink) {
                coloredTextLogo.append(WHITE);
                isPink = false;
            }
            coloredTextLogo.append(c);
        }
        coloredTextLogo.append(RESET).append('\n');
        System.out.println(coloredTextLogo);
    }

    public static void printLogo() {
        printLogo(!isWindowsSystem);
    }

    public static void printTitle(boolean color, boolean authors) {
        System.out.println("*****************************");
        if (color)
            System.out.print(PINK + "JIR" + WHITE + "Send" + RESET);
        else
            System.out.print("JIRSend");
        System.out.println(" - Local ChatSystem");

        if (authors) {
            System.out.println("    - by:");
            System.out.println("          Victor LASSERRE");
            System.out.println("          Valentin SERVIERES");
        }

        System.out.println("*****************************\n");
    }

    public static void printTitle(boolean authors) {
        printTitle(!isWindowsSystem, authors);
    }
}
