package org.echocat.tjl;

import org.echocat.tjl.util.ExtendedLogRecord;

import java.io.OutputStream;
import java.security.PrivilegedAction;
import java.util.Optional;
import java.util.logging.*;

import static java.lang.ClassLoader.getSystemClassLoader;
import static java.lang.System.getProperty;
import static java.lang.System.getenv;
import static java.security.AccessController.doPrivileged;
import static java.util.Optional.ofNullable;
import static java.util.logging.Level.ALL;
import static java.util.logging.Level.parse;
import static java.util.logging.LogManager.getLogManager;

public class Handler extends StreamHandler {

    public Handler() {
        this(System.out);
    }

    public Handler(OutputStream out) {
        super(out, new JsonFormatter());
        init();
    }

    @Override
    public synchronized void publish(LogRecord record) {
        final ExtendedLogRecord extended = new ExtendedLogRecord(record);
        extended.detectAndSetSource(className -> !getClass().getName().equals(className));
        super.publish(extended);
        flush();
    }

    @Override
    public synchronized void close() throws SecurityException {
        flush();
    }

    protected void init() {
        final Formatter formatter = determineFormatter();
        final Level level = determineLevel();
        patchGlobalLevelIfRequired();

        doPrivileged((PrivilegedAction<Void>) () -> {
            setLevel(level);
            setFormatter(formatter);
            return null;
        });
    }

    protected Formatter determineFormatter() {
        return findSystemProperty("log.format", "LOG_FORMAT")
            .map(String::trim)
            .map(String::toUpperCase)
            .map(this::formatToFormatter)
            .orElseGet(() -> findProperty(getClass(), "formatter")
                .map(this::newInstanceOf)
                .filter(candidate -> candidate instanceof Formatter)
                .map(candidate -> (Formatter) candidate)
                .orElseGet(JsonFormatter::new)
            );
    }

    protected Level determineLevel() {
        return findProperty(getClass(), "level")
            .map(value -> parse(value.trim()))
            .orElse(ALL);
    }

    protected void patchGlobalLevelIfRequired() {
        findSystemProperty("log.level", "LOG_LEVEL")
            .map(String::trim)
            .map(String::toUpperCase)
            .map(Level::parse)
            .ifPresent(level -> getLogManager().getLogger("").setLevel(level));
    }

    protected Object newInstanceOf(String className) {
        try {
            //noinspection deprecation
            return getSystemClassLoader().loadClass(className).newInstance();
        } catch (final Exception e) {
            return new IllegalStateException("Cannot create instance of '" + className + "'.", e);
        }
    }

    protected Formatter formatToFormatter(String format) {
        if (format.equals("JSON")) {
            return new JsonFormatter();
        }
        if (format.equals("TEXT")) {
            return new TextFormatter();
        }
        throw new IllegalArgumentException("Illegal format: " + format);
    }

    protected static Optional<String> findProperty(Class<?> owner, String name) {
        final String propertyName = owner.getName() + "." + name;
        return ofNullable(getLogManager().getProperty(propertyName));
    }

    protected static Optional<String> findSystemProperty(String propName, String envName) {
        String value = getProperty(propName);
        if (value == null) {
            value = getenv(envName);
        }
        return ofNullable(value);
    }

}
