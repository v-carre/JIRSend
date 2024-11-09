package com.JIRSend.ui;

public class Log {
    public final static int ERROR_ONLY = 0;
    public final static int ERROR_AND_WARNING = 1;
    public final static int ALL = 99;
    public final static int ERROR = 0;
    public final static int WARNING = 1;
    public final static int LOG = 2;
    public final static int DEBUG = 3;
    private static boolean verbose = false;
    private static int displayTreshold = Integer.MAX_VALUE;

    /**
     * Set wether debug & errors are printed to console
     * If they are, there will be printed only if their priority is <= threshold
     * (default INT_MAX)
     * 
     * @param on
     * @param threshold
     */
    public static void setVerbose(boolean on, int threshold) {
        verbose = on;
        displayTreshold = threshold;
    }

    /**
     * Set wether debug & errors are printed to console
     * 
     * @param on
     */
    public static void setVerbose(boolean on) {
        verbose = on;
    }

    public static void setThreshold(int threshold) {
        displayTreshold = threshold;
    }

    /**
     * Will println out string if verbose is active and if indicationLevel <=
     * displayThreshold = Integer.MAX_VALUE
     * 
     * @param string
     * @param indicationLevel (default 0)
     */
    public static void l(String string, int indicationLevel) {
        if (verbose && indicationLevel <= displayTreshold)
            System.out
                    .println(printWithStack(false, string, indicationLevel, Thread.currentThread().getStackTrace()[2]));
    }

    /**
     * Will println out string if displayThreshold >= 0
     * 
     * @param string
     */
    public static void l(String string) {
        if (verbose && DEBUG <= displayTreshold)
            System.out.println(printWithStack(false, string, DEBUG, Thread.currentThread().getStackTrace()[2]));
    }

    /**
     * Will println err string if verbose is active and if indicationLevel <=
     * displayThreshold = Integer.MAX_VALUE
     * 
     * @param string
     * @param indicationLevel (default 0)
     */
    public static void e(String err, int indicationLevel) {
        if (verbose && indicationLevel <= displayTreshold)
            System.err.println(printWithStack(true, err, indicationLevel, Thread.currentThread().getStackTrace()[2]));
    }

    /**
     * Will println err string if displayThreshold >= 0
     * 
     * @param string
     */
    public static void e(String err) {
        if (verbose && ERROR <= displayTreshold)
            System.err.println(printWithStack(true, err, ERROR, Thread.currentThread().getStackTrace()[2]));
    }

    private static String levelToString(int indicationLevel) {
        switch (indicationLevel) {
            case ERROR:
                return "❌ERROR";
            case WARNING:
                return "⚠️ WARNING";
            case LOG:
                return "LOG";
            case DEBUG:
                return "DEBUG";
            default:
                return "OTHER";
        }
    }

    private static String printWithStack(boolean isErr, String string, int indicationLevel, StackTraceElement stack) {
        String className = stack.getClassName();
        String classNameWithoutCom = (className.startsWith("com.JIRSend.")
                ? className.substring("com.JIRSend.".length())
                : className);
        return (isErr ? "⛔ " : "") + "[" + levelToString(indicationLevel) + "] (" +
                classNameWithoutCom + "->" + stack.getMethodName() + ") " + string;
    }
}
