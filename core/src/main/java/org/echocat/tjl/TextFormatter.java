package org.echocat.tjl;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import static java.lang.System.lineSeparator;
import static org.echocat.tjl.LogEvent.logObject;

public class TextFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        return toLogObject(record).formatAsText() + lineSeparator();
    }

    protected LogEvent toLogObject(LogRecord record) {
        return logObject()
            .basedOn(record)
            .build();
    }

}
