package com.JIRSend.controller;

import java.util.ArrayList;

public class Pipe<T> {
    private String pipeName;
    private boolean isSending;
    private ArrayList<Subscription<T>> subs;

    public Pipe(String name) {
        this.pipeName = name;
        this.subs = new ArrayList<>();
    }

    public synchronized void put(T message) throws InfinitePipeRecursion {
        if(isSending) //Note: we may want to reset isSending at this point
            throw new InfinitePipeRecursion(null);
           
        isSending = true;
        for (Subscription<T> subscription : subs) {
            subscription.get(message);
        }
        isSending=false;
    }

    public synchronized void safePut(T message) {
        if(isSending)
            return;
           
        isSending = true;
        for (Subscription<T> subscription : subs) {
            subscription.get(message);
        }
        isSending=false;
    }

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
}
