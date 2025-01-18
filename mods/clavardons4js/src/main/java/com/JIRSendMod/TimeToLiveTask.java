package com.JIRSendMod;

import java.util.TimerTask;

/*
 *
 * NOTE:
 * MOST OF THIS CODE IS A COPY-PASTE 
 * FROM chatsystem-anglade-loubejac !
 * (as it is a mod to communicate with their chatsystem)
 * 
 */

public class TimeToLiveTask  extends TimerTask {
    private final MyNetworkInterface myNetworkInterface;
    private final UserList userList= UserList.getInstance();


    public TimeToLiveTask(MyNetworkInterface myNetworkInterface) {
        this.myNetworkInterface = myNetworkInterface;
    }

    @Override
    public void run() {
        myNetworkInterface.sendTimeToLive();
        userList.decrementTTL();
    }

}
