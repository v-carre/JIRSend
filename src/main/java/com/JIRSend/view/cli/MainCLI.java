package com.JIRSend.view.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.JIRSend.controller.MainController;
import com.JIRSend.model.user.UserEntry;
import com.JIRSend.view.MainAbstractView;

public class MainCLI extends MainAbstractView {

    protected MainController controller;
    private MainCliThread thread;

    public MainCLI(MainController controller) {
        this.controller = controller;
        this.thread = new MainCliThread();
    }

    @Override
    public void open() {
        Log.l("Starting CLI thread");
        this.thread.start();
    }

    private void commandHelperPrint(String cmd, String description) {
        System.out.println(CliTools.colorize(CliTools.NORMAL, " > ") +
                CliTools.colorize(CliTools.PURPLE_NORMAL_COLOR + CliTools.UNDERLINED + CliTools.BLACK_NORMAL_BACKGROUND,
                        cmd)
                + CliTools.colorize(CliTools.NORMAL, " - " + description) + "\n");
    }

    private void commandHelperPrint(String cmd, String params, String description) {
        System.out.println(CliTools.colorize(CliTools.NORMAL, " > ") +
                CliTools.colorize(CliTools.PURPLE_NORMAL_COLOR + CliTools.UNDERLINED + CliTools.BLACK_NORMAL_BACKGROUND,
                        cmd)
                + " " + CliTools.colorize(CliTools.BLACK_NORMAL_BACKGROUND + CliTools.WHITE_NORMAL_COLOR, params)
                + CliTools.colorize(CliTools.NORMAL, " - " + description) + "\n");
    }

    private class MainCliThread extends Thread {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        private String readIn() {
            try {
                return reader.readLine();
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        public void run() {
            this.setName("CLI Thread");

            chooseUsername();

            System.out.println("Welcome " + CliTools.colorize(CliTools.BOLD, controller.getUsername()) + "!");
            CliTools.coloredPrintln(CliTools.BLACK_DESAT_COLOR, controller.getContacts().size()
                    + " people connected.\n");

            System.out.println(CliTools.colorize(CliTools.PURPLE_NORMAL_COLOR, "Type ") +
                    CliTools.colorize(
                            CliTools.PURPLE_NORMAL_COLOR + CliTools.BOLD + CliTools.UNDERLINED
                                    + CliTools.BLACK_NORMAL_BACKGROUND,
                            "help")
                    +
                    CliTools.colorize(CliTools.PURPLE_DESAT_COLOR, " to get the list of the available commands."));
            while (true) {
                CliTools.coloredPrint(CliTools.PURPLE_NORMAL_COLOR, "> ");
                String cmd = readIn();
                String[] args = cmd.split(" ");
                commandHandler(args);
            }
        }

        private void commandHandler(String[] args) {
            if (args.length == 0 || args[0].equals(""))
                return;

            switch (args[0].toLowerCase()) {
                case "help":
                case "h":
                    System.out.println(CliTools.colorize(CliTools.PURPLE_NORMAL_COLOR + CliTools.BOLD, "===== ") +
                            CliTools.colorize(
                                    CliTools.WHITE_DESAT_COLOR + CliTools.UNDERLINED + CliTools.BOLD
                                            + CliTools.BLACK_NORMAL_BACKGROUND,
                                    "HELP")
                            +
                            CliTools.colorize(CliTools.PURPLE_NORMAL_COLOR + CliTools.BOLD, " =====") + "\n");
                    commandHelperPrint("h, help", "prints this message");
                    commandHelperPrint("q, quit", "quit JIRSend");
                    commandHelperPrint("lc, list-contacts", "list contacts");
                    commandHelperPrint("dm, direct-message", "<username> <message>", "send direct message");

                    CliTools.coloredPrintln(CliTools.PURPLE_NORMAL_COLOR + CliTools.BOLD, "================\n");
                    break;

                case "list-contacts": // [fallthrough]
                case "lc":
                    System.out.println(CliTools.colorize(CliTools.PURPLE_NORMAL_COLOR + CliTools.BOLD, "===== ") +
                            CliTools.colorize(
                                    CliTools.WHITE_DESAT_COLOR + CliTools.UNDERLINED + CliTools.BOLD
                                            + CliTools.BLACK_NORMAL_BACKGROUND,
                                    "USERS LIST")
                            +
                            CliTools.colorize(CliTools.PURPLE_NORMAL_COLOR + CliTools.BOLD, " =====") + "\n");

                    CliTools.coloredPrintln(CliTools.BLACK_DESAT_COLOR,
                            controller.getContacts().size() + " people connected.\n");
                    for (UserEntry ue : controller.getContacts()) {
                        System.out.println(" - " + ue.username + " (" +
                                (ue.online ? CliTools.colorize(CliTools.GREEN_NORMAL_COLOR, "ONLINE")
                                        : CliTools.colorize(CliTools.RED_NORMAL_COLOR, "OFFLINE"))
                                + ")");
                    }

                    CliTools.coloredPrintln(CliTools.PURPLE_NORMAL_COLOR + CliTools.BOLD, "======================\n");
                    break;

                case "direct-message":
                case "dm":
                    System.out.println(CliTools.colorize(CliTools.RED_NORMAL_COLOR, "The command '") +
                            CliTools.colorize(
                                    CliTools.RED_DESAT_COLOR + CliTools.UNDERLINED + CliTools.BLACK_NORMAL_BACKGROUND,
                                    args[0])
                            +
                            CliTools.colorize(CliTools.RED_NORMAL_COLOR, "' is not available yet."));
                    break;

                case "quit":
                case "q":
                    CliTools.coloredPrintln(CliTools.PURPLE_NORMAL_COLOR, "Exiting JIRSend...");
                    System.exit(0);
                    break;

                default:
                    System.out.println(CliTools.colorize(CliTools.RED_NORMAL_COLOR, "Unknown command '") +
                            CliTools.colorize(
                                    CliTools.RED_DESAT_COLOR + CliTools.UNDERLINED + CliTools.BLACK_NORMAL_BACKGROUND,
                                    args[0])
                            +
                            CliTools.colorize(CliTools.RED_NORMAL_COLOR, "'.") + "\n" +
                            CliTools.colorize(CliTools.PURPLE_DESAT_COLOR, "Please type ") +
                            CliTools.colorize(CliTools.PURPLE_NORMAL_COLOR + CliTools.UNDERLINED, "help") +
                            CliTools.colorize(CliTools.PURPLE_DESAT_COLOR,
                                    " to get the list of the available commands."));
                    break;
            }
        }

        private void chooseUsername() {
            while (true) {
                System.out.print("Enter your username: ");
                String usernameChosen = readIn();
                String res = controller.changeUsername(usernameChosen);
                if (res.equals("")) {
                    break;
                }

                CliTools.printBigError(res);
            }
        }
    }
}
