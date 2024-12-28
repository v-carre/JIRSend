package com.JIRSendApp.controller;

@FunctionalInterface
public interface Subscription<T> {
    /**
     * Subscription callback
     * @param value
     */
    public void get(T value);
}