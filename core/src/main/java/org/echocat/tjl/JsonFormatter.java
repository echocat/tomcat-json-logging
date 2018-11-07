package org.echocat.tjl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.echocat.tjl.util.OptionalTypeAdapter;

import java.util.Optional;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import static java.lang.System.lineSeparator;
import static org.echocat.tjl.Constants.ISO_DATE_FORMAT;
import static org.echocat.tjl.LogEvent.logObject;

@SuppressWarnings("WeakerAccess")
public class JsonFormatter extends Formatter {

    private final Gson gson;

    public JsonFormatter() {
        this(new GsonBuilder()
            .setDateFormat(ISO_DATE_FORMAT)
            .registerTypeAdapter(Optional.class, new OptionalTypeAdapter())
            .create()
        );
    }

    protected JsonFormatter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public String format(LogRecord record) {
        return toJson(toLogObject(record)) + lineSeparator();
    }

    protected LogEvent toLogObject(LogRecord record) {
        return logObject()
            .basedOn(record)
            .build();
    }

    protected String toJson(LogEvent object) {
        return gson().toJson(object);
    }

    protected Gson gson() {
        return gson;
    }

}
