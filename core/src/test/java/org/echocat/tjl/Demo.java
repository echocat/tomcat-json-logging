package org.echocat.tjl;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;

class Demo {

    static {
        try (final InputStream is = Demo.class.getClassLoader().getResourceAsStream("demo-logging.properties")) {
            LogManager.getLogManager().readConfiguration(is);
        } catch (final IOException e) {
            Logger.getAnonymousLogger().log(SEVERE, "Cannot load demo-logging.properties", e);
        }
    }

    private final Logger LOG = Logger.getLogger(Demo.class.getName());

    @Test
    void all() {
        LOG.log(WARNING, "Something is not that problematic.", new Throwable("foo", new Exception("bar")));
        LOG.severe("Something goes wrong.");
        LOG.info("Hello world!");
        LOG.fine("This is fine");
        LOG.finer("This is finer");
        LOG.finest("This is finest");
    }

}