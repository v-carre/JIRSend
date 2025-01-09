package com.JIRSendMod;

import java.util.TimerTask;

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
