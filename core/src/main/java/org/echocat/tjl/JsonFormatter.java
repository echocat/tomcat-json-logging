package org.echocat.tjl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.echocat.tjl.util.OptionalTypeAdapter;

import java.util.Optional;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import static org.echocat.tjl.LogObject.logObject;

@SuppressWarnings("WeakerAccess")
public class JsonFormatter extends Formatter {

    private final Gson gson;

    public JsonFormatter() {
        this(new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .registerTypeAdapter(Optional.class, new OptionalTypeAdapter())
            .create()
        );
    }

    protected JsonFormatter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public String format(LogRecord record) {
        return toJson(toLogObject(record)) + "\n";
    }

    protected LogObject toLogObject(LogRecord record) {
        return logObject()
            .basedOn(record)
            .build();
    }

    protected String toJson(LogObject object) {
        return gson().toJson(object);
    }

    protected Gson gson() {
        return gson;
    }

}
