package com.JIRSendMod;

import com.JIRSendAPI.ModController;
import com.JIRSendAPI.ModMessage;
import com.JIRSendAPI.ModUser;

public class OnReveive implements UserList.Observer {
    private final Clavardons4JS controller;

    public OnReveive(Clavardons4JS controller) {
        this.controller = controller;
    }

    public void updateUserList(String type, String[] args) {
        //System.out.println("GOT MESSAGE FROM CLAVARDONS:\n[" + type + "] " + String.join(" ", args));
        User user;
        switch (type) {
            case "setUsername":
                user = UserList.getInstance().getActiveUserByUsername(args[0]);
                ModController.contactChange.put(new ModUser(Clavardons4JS.MOD_INFO,
                        user.modUser.userID,
                        user.modUser.username,
                        ModUser.Status.Online));
                break;
            case "setUserOffline":
                user = UserList.getInstance().getActiveUserByUsername(args[0]);
                ModController.contactChange.put(new ModUser(Clavardons4JS.MOD_INFO,
                        user.modUser.userID,
                        user.modUser.username,
                        ModUser.Status.Offline));
                break;
            case "addUser":
                user = UserList.getInstance().getUserByUsername(args[1]);
                ModController.contactChange.put(new ModUser(Clavardons4JS.MOD_INFO,
                        user.modUser.userID,
                        user.modUser.username,
                        ModUser.Status.Online));
                break;
            case "connectTCP":
                user = UserList.getInstance().getUserByUsername(args[0]);
                ModController.contactChange.put(new ModUser(Clavardons4JS.MOD_INFO,
                        user.modUser.userID,
                        user.modUser.username,
                        ModUser.Status.Online));
                user.getSocket().sendConnectResponse(true);
                break;
            case "AcceptSession":
                user = UserList.getInstance().getUserByUsername(args[0]);
                ModController.contactChange.put(new ModUser(Clavardons4JS.MOD_INFO,
                        user.modUser.userID,
                        user.modUser.username,
                        ModUser.Status.Online));
                break;
            case "RefuseSession":
                user = UserList.getInstance().getUserByUsername(args[0]);
                ModController.contactChange.put(new ModUser(Clavardons4JS.MOD_INFO,
                        user.modUser.userID,
                        user.modUser.username,
                        ModUser.Status.Busy));
                break;
            case "messageTCP":
                User me = UserList.getInstance().getMe();
                user = UserList.getInstance().getUserByUsername(args[0]);
                ModController.storeMessage.put(new ModMessage(Clavardons4JS.MOD_INFO, user.modUser.userID,
                        user.modUser.username, me.modUser.userID, args[1], Clavardons4JS.getTime(), true));
                break;
            case "quitTCP":
                user = UserList.getInstance().getUserByUsername(args[0]);
                ModController.contactChange.put(new ModUser(Clavardons4JS.MOD_INFO,
                        user.modUser.userID,
                        user.modUser.username,
                        ModUser.Status.Busy));
                break;
            default:
                System.err.println("Received unknown message from Clavardon: ["+type+"] "+String.join(" ",args));
        }
    }
}
