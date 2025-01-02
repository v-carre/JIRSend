package com.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import com.JIRSendAPI.*;
import com.JIRSendAPI.ModUser.Status;

public class Compagny implements JIRSendMod {
    private static final Random random = new Random();
    private final Status[] status = { Status.Away, Status.Online, Status.Online, Status.Online, Status.Online,
            Status.Offline, Status.Busy, Status.Busy, Status.Offline, Status.Away, Status.Online };
    private final String[] users = { "John", "Johnson", "Michel", "Joe", "Johnattan", "Jean-Philippe", "JP", "Josianne",
            "Anne", "Jeanne", "Lucianne", "Michelle", "Martine", "Francis", "Claude", "Guillaume", "Hacker", "Baptiste",
            "IGS" };
    private final String[] dialogs = {
            "Connaissez-vous l'IGS ?",
            "Va falloir mettre les bouchées doubles là...",
            "On est dans le rouge là",
            "On va être sur la corde raide si ça continue dans cette predi",
            "Est-ce que tu peux m'aider mettre la main à la pâte ?",
            "Bon on va jouer cartes sur tables. Je propose une réu avec tous les collaborateurs ce soir à 19h30.",
            "Faut arrêter de tourner autour du pot là !",
            "Hier on a mis les points sur les i. Maintenant il faut go forward.",
            "J'ai passé un savon à la startup a qui ont fait sous traiter le process car ça va pas du tout là, ils respectent pas du tout le cdc.",
            "Va falloir être au taquet, on a du pain sur la planche",
            "Content de voir qu'on est sur la même longueur d'onde",
            "La concurrence nous a dans le collimateur. Il faut qu'on continue d'avoir une longueur d'avance",
            "Arrête de faire cavalier seul !",
            "On est tous dans le même bateau, il faut prendre le pli",
            "Va falloir faire une croix sur la prochaine fonctionnalité.",
            "T'as vu le JIRA ?",
            "Ah t'es connecté ? Tu tombes à pic",
            "Ce projet commence à trainer en longueur, c'est pas le moment de lever le pied !"
    };
    private ModController controller;

    private String getTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

    private final JIRSendModInformation MOD_INFO = new JIRSendModInformation(
            "compagny",
            "Compagny",
            "Un mod pour avoir de la compagnie comme en entreprise",
            "MagicTINTIN", // author
            1, // interface version
            0, // mod version
            new ImageIcon(getClass().getResource("/assets/compagny.png")));

    @Override
    public void initialize(ModController controller) {
        this.controller = controller;
        System.out.println("Compagny initialized");
    }

    @Override
    public void stop() {
    }

    @Override
    public JIRSendModInformation getModInformation() {
        return MOD_INFO;
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return username.length() <= 20 && username.matches("^[a-zA-Z0-9_-]+$");
    }

    @Override
    public void changeUsername(String username) {
    }

    @Override
    public void sendMessage(String recipientID, String message) {
        ModController.storeMessage.put(new ModMessage(MOD_INFO, "local", controller.mainController.getUsername(),
                recipientID, message, getTime(), false));
    }

    @Override
    public void connected() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        System.out.println("Simulation prepared !");

        executorService.scheduleAtFixedRate(() -> {
            try {
                executeRandomAction();

                // Sleep for a random interval between 1 and 8 seconds
                int randomDelay = random.nextInt(8) + 1;
                randomDelay = 1;
                Thread.sleep(randomDelay * 1000L); // Convert to milliseconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, 0, 1, TimeUnit.MILLISECONDS); // Base interval is small, delays handled manually

        System.out.println("Simulation started !");
        // ModController.contactChange
        //         .put(new ModUser(MOD_INFO, users[0], users[0], status[0]));
    }

    private void executeRandomAction() {
        System.out.print("Simulation action : ");
        int actionIndex = random.nextInt(2); // There are 6 actions (0 to 5)
        switch (actionIndex) {
            case 0:
                contactChange();
                break;
            case 1:
                newMessage();
                break;
            default:
                break;
        }
    }

    private void contactChange() {
        int randomStatus = random.nextInt(status.length);
        int randomName = random.nextInt(users.length);
        if (!controller.mainController.isUsernameAvailable(users[randomName], MOD_INFO)) return;
        System.out.println(users[randomName]);
        ModController.contactChange
                .put(new ModUser(MOD_INFO, users[randomName], users[randomName], status[randomStatus]));
    }

    private void newMessage() {
        int randomName = random.nextInt(users.length);
        int randomDialog = random.nextInt(dialogs.length);
        if (!controller.mainController.isUsernameAvailable(users[randomName], MOD_INFO)) return;
        System.out.println(users[randomName]);
        ModController.contactChange
                .put(new ModUser(MOD_INFO, users[randomName], users[randomName], Status.Online));
        ModController.storeMessage.put(new ModMessage(MOD_INFO, users[randomName], users[randomName],
                "local", dialogs[randomDialog], getTime(), true));
    }
}
