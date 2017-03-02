package com.visus.logging;

import com.visus.entities.User;

import android.util.Log;

/**
 * Created by jonathanperry on 27/02/2017.
 */
public class Logger {

    private static String tag;
    private static Object message;

    public Logger() {

    }

    public Logger(String tag, Object message) {
        this.tag = tag;
        this.message = message;
    }

    public Logger(String tag, String message) {
        this.tag = tag;
        this.message = message;
    }

    public static void log() {
        if (tag != null && message != null)
            Log.e(tag + " ", message.toString());
        else
            throw new IllegalArgumentException("Tag and Message parameters have not been set");
    }

    public static void log(String tag, String message) {
        Log.e(tag + " | ", message);
    }

    public static void log(String tag, Object message) {
        Log.e(tag + " | ", message.toString());
    }

}
