package org.echocat.tjl;

import java.util.NoSuchElementException;
import java.util.logging.Level;

public enum LogLevel {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
    FATAL;

    public static LogLevel valueOf(Level jdkLevel) throws NoSuchElementException {
        if (jdkLevel.intValue() <= Level.FINER.intValue()) {
            return TRACE;
        }
        if (jdkLevel.intValue() <= Level.FINE.intValue()) {
            return DEBUG;
        }
        if (jdkLevel.intValue() <= Level.INFO.intValue()) {
            return INFO;
        }
        if (jdkLevel.intValue() <= Level.WARNING.intValue()) {
            return WARN;
        }
        if (jdkLevel.intValue() <= Level.SEVERE.intValue()) {
            return ERROR;
        }
        return FATAL;
    }

}
