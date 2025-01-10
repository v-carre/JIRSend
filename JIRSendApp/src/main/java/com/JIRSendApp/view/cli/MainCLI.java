package com.JIRSendApp.view.cli;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.history.DefaultHistory;

import com.JIRSendApp.controller.MainController;
import com.JIRSendApp.model.Message;
import com.JIRSendApp.model.user.UserEntry;
import com.JIRSendApp.model.user.UserEntry.Status;
import com.JIRSendApp.view.MainAbstractView;

public class MainCLI extends MainAbstractView {
    protected MainController controller;
    private MainCliThread thread;
    private boolean connected = false;
    private List<String> dmTargets = new ArrayList<>();

    protected List<String> commands = Arrays.asList("h", "help", "q", "quit", "lc", "list-contacts", "dm",
            "direct-message",
            "su", "switch-username");
    protected History history = new DefaultHistory();

    protected Completer superCompleter = (reader, line, candidates) -> {
        ParsedLine parsedLine = line;
        List<String> words = parsedLine.words();
        if (words.size() == 1) {
            // complete the main commands only
            commands.forEach(command -> candidates.add(new Candidate(command)));
        } else if (words.size() == 2 && (words.get(0).equals("dm") || words.get(0).equals("direct-message"))) {
            // dm suggestions based on userlist
            dmTargets.forEach(target -> candidates.add(new Candidate(target)));
        }
        // no completion for 3 params or more
    };

    protected LineReader reader;

    // parser that ignores '!'' for history expansion (causes crash)
    protected DefaultParser parser = new DefaultParser();

    public MainCLI(MainController controller) {
        this.controller = controller;
        this.connected = false;

        parser.setEofOnUnclosedBracket(DefaultParser.Bracket.CURLY, DefaultParser.Bracket.ROUND,
                DefaultParser.Bracket.SQUARE);
        parser.setEscapeChars(null); // Avoids special handling of `!`
        this.reader = LineReaderBuilder.builder().history(history).parser(parser).completer(superCompleter).build();

        // disable history expansion on the LineReader (causes crash)
        reader.setOpt(LineReader.Option.DISABLE_EVENT_EXPANSION);

        this.thread = new MainCliThread();
        MainController.contactsChange.subscribe((messageReceived) -> {
            dmTargets = controller.getConnectedUsernames();
            if (connected)
                printIncomingMessage(CliTools.colorize(CliTools.BLACK_DESAT_COLOR, messageReceived));
        });
        MainController.messageReceived.subscribe((msg) -> {
            messageReceived(msg);
        });
    }

    @Override
    public void start() {
        Log.l("Loading JIRSend");
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

    private void printIncomingMessage(String message) {
        System.out.print("\r" + message + "\n" + commandInput);
    }

    private void messageReceived(Message msg) {
        printIncomingMessage(
                "[" + CliTools.colorize(CliTools.PURPLE_NORMAL_COLOR + CliTools.BOLD, msg.sender) + "] " + msg.message);
    }

    private class MainCliThread extends Thread {
        // pfff this is so old school !
        BufferedReader oldReader = new BufferedReader(new InputStreamReader(System.in));

        private String readIn() {
            try {
                return reader.readLine(commandInput).trim();
            } catch (Exception e) {
                // e.printStackTrace();
                if (e instanceof UserInterruptException || e instanceof EndOfFileException) {
                    CliTools.coloredPrintln(CliTools.PURPLE_NORMAL_COLOR, "Exiting JIRSend...");
                    controller.stoppingApp();
                }
                return null;
            }
        }

        private String oldReadIn() {
            try {
                return oldReader.readLine().trim();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public void run() {
            this.setName("CLI Thread");

            chooseUsername(false);
            dmTargets = controller.getConnectedUsernames();

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
                String cmd = readIn();
                if (cmd == null)
                    continue;
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
                                userStatus(ue.online)
                                + ")");
                    }

                    CliTools.coloredPrintln(CliTools.PURPLE_NORMAL_COLOR + CliTools.BOLD, "======================\n");
                    break;

                case "direct-message":
                case "dm":
                    sendMessage(args);
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
                    controller.stoppingApp();
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

        private static String userStatus(Status status) {
            switch (status) {
                case Offline:
                    return CliTools.colorize(CliTools.RED_NORMAL_COLOR, "OFFLINE");
                case Busy:
                    return CliTools.colorize(CliTools.RED_NORMAL_COLOR, "BUSY");
                case Away:
                    return CliTools.colorize(CliTools.YELLOW_NORMAL_COLOR, "AWAY");
                case Online:
                    return CliTools.colorize(CliTools.GREEN_NORMAL_COLOR, "ONLINE");

                default:
                    return CliTools.colorize(CliTools.GREEN_NORMAL_COLOR, "ONLINE");
            }
        }

        private void chooseUsername(boolean change) {
            while (true) {
                System.out.print("Enter your " + (change ? "new " : "") + "username: ");
                String usernameChosen = oldReadIn();
                if (usernameChosen == null) {
                    System.out.println("\r");
                    continue;
                }
                if (usernameChosen != null && usernameChosen.equals(controller.getUsername())) {
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

        private void sendMessage(String[] argv) {
            final String dest;
            final String msg;

            if (argv.length < 2) { // no arg
                System.out.println("Please enter recipient's username: ");
                dest = oldReadIn();
            } else {
                dest = argv[1];
            }

            boolean found = false;
            boolean isOnline = false;
            for (UserEntry entry : controller.getContacts()) {
                if (entry.username.equals(dest)) {
                    found = true;
                    isOnline = entry.online();
                    break;
                }
            }

            if (!found) {
                System.out.println(CliTools.colorize(CliTools.RED_NORMAL_COLOR, "No user \"" + dest + "\" was found."));
                return;
            } else if (!isOnline) {
                System.out.println(CliTools.colorize(CliTools.RED_NORMAL_COLOR, "User " + dest + " is not online."));
                return;
            }

            if (argv.length < 3) {
                System.out.println("Please enter your message: ");
                msg = oldReadIn();
            } else {
                String acu = "";
                for (int i = 2; i < argv.length; ++i)
                    acu = acu + argv[i] + ' ';
                msg = acu;
            }

            if (msg.isEmpty() || msg.isBlank()) return;

            MainController.sendMessage.safePut(new Message(controller.getUsername(), dest, msg, controller.getTime()));
        }
    }
}
