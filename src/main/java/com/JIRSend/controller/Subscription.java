package com.JIRSend.controller;

@FunctionalInterface
public interface Subscription<T> {
    /**
     * Subscription callback
     * @param value
     */
    public void get(T value);
}