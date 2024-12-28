package com.JIRSendApp.model.network;

import java.net.InetAddress;

public abstract class NetCallback {
    public abstract void execute(InetAddress senderAddress, int senderPort, String value, boolean isBroadcast,boolean isUDP);
}
