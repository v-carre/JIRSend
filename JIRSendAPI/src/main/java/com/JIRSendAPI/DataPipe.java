package com.JIRSendAPI;

import java.util.ArrayList;

public class DataPipe<T> {
    private String pipeName;
    private boolean isSending;
    private ArrayList<Subscription<T>> subs;

    public DataPipe(String name) {
        this.pipeName = name;
        this.subs = new ArrayList<>();
    }

    /**
     * Send a message to all DataPipe instance subscribers
     * 
     * @param message
     */
    public synchronized void put(T message) {
        if (isSending)
            return;

        isSending = true;
        for (Subscription<T> subscription : subs) {
            subscription.get(message);
        }
        isSending = false;
    }

    /**
     * Subscribe to execute subscription on new message
     * 
     * @param sub
     */
    public synchronized void subscribe(Subscription<T> sub) {
        this.subs.add(sub);
    }

    public String getName() {
        return pipeName;
    }

    @Override
    public String toString() {
        return "[" + pipeName + "]";
    }

    @FunctionalInterface
    public static interface Subscription<T> {
        /**
         * Subscription callback
         * 
         * @param value
         */
        public void get(T value);
    }
}
