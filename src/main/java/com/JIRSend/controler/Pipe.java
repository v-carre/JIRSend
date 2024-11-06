package com.JIRSend.controler;

import java.util.ArrayList;

public class Pipe<T> {
    private String pipeName;
    private boolean isSending;
    private ArrayList<Subscription<T>> subs;

    public Pipe(String name) {
        this.pipeName = name;
    }

    public void put(T message) throws InfinitePipeRecursion {
        if(isSending)
            throw new InfinitePipeRecursion(null);
           
        isSending = true;
        for (Subscription<T> subscription : subs) {
            subscription.get(message);
        }
        isSending=false;
    }

    public String getName() {
        return pipeName;
    }

    @Override
    public String toString() {
        return "[" + pipeName + "]";
    }
}
