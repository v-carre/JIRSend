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
    private boolean connected = false;

    public MainCLI(MainController controller) {
        this.controller = controller;
        this.connected = false;
        this.thread = new MainCliThread();
        MainController.contactsChange.subscribe((messageReceived) -> {
            if (connected)
            printIncomingMessage(CliTools.colorize(CliTools.BLACK_DESAT_COLOR, messageReceived));
        });
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

    private final String commandInput = CliTools.colorize(CliTools.PURPLE_NORMAL_COLOR, "> ");

    private void printCommandInput() {
        System.out.print(commandInput);
    }

    private void printIncomingMessage(String message) {
        System.out.print(message + "\n" + commandInput);
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

            chooseUsername(false);

            System.out.println("Welcome " + CliTools.colorize(CliTools.BOLD, controller.getUsername()) + "!");
            CliTools.coloredPrintln(CliTools.BLACK_DESAT_COLOR, controller.getNumberConnected()
                    + " people connected.\n");

            System.out.println(CliTools.colorize(CliTools.PURPLE_NORMAL_COLOR, "Type ") +
                    CliTools.colorize(
                            CliTools.PURPLE_NORMAL_COLOR + CliTools.BOLD + CliTools.UNDERLINED
                                    + CliTools.BLACK_NORMAL_BACKGROUND,
                            "help")
                    +
                    CliTools.colorize(CliTools.PURPLE_DESAT_COLOR, " to get the list of the available commands."));
            while (true) {
                printCommandInput();
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
                    commandHelperPrint("su, switch-username", "[newUsername]", "change your username");

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
                            controller.getNumberConnected() + " people connected.\n");
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

                case "switch-username":
                case "su":
                    if (args.length >= 2)
                        chooseUsername(true, args[1]);
                    else
                        chooseUsername(true);
                    System.out.println("You will now appear as "
                            + CliTools.colorize(CliTools.BOLD, controller.getUsername()) + ".");
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

        private void chooseUsername(boolean change) {
            while (true) {
                System.out.print("Enter your " + (change ? "new " : "") + "username: ");
                String usernameChosen = readIn();
                if (usernameChosen.equals(controller.getUsername()) && usernameChosen != null) {
                    connected = true;
                    break;
                }
                String res = controller.changeUsername(usernameChosen);
                if (res.equals("")) {
                    connected = true;
                    break;
                }

                CliTools.printBigError(res);
            }
        }

        private void chooseUsername(boolean change, String newUsername) {
            if (newUsername.equals(controller.getUsername()) && newUsername != null) {
                connected = true;
                return;
            }
            String res = controller.changeUsername(newUsername);
            if (res.equals("")) {
                connected = true;
                return;
            }

            CliTools.printBigError(res);
            // if not available go into loop
            chooseUsername(change);
        }
    }
}
