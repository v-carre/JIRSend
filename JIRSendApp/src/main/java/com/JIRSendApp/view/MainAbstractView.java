package com.JIRSendApp.view;

import java.awt.HeadlessException;
import java.lang.reflect.InvocationTargetException;

public abstract class MainAbstractView {
    public abstract void start() throws HeadlessException, InvocationTargetException, InterruptedException;
    public abstract void open();
}
