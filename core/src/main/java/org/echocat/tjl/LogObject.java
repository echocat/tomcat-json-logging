package org.echocat.tjl;

import com.google.gson.annotations.SerializedName;
import org.apache.juli.OneLineFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.*;

public class LogObject {

    private final static Formatter INTERNAL_FORMATTER = new OneLineFormatter();

    public static Builder logObject() {
        return new Builder();
    }

    @SerializedName("when")
    private final Date when;
    @SerializedName("level")
    private final String level;
    @SerializedName("levelId")
    private final int levelId;
    @SerializedName("source")
    private final Optional<String> source;
    @SerializedName("logger")
    private final String logger;
    @SerializedName("threadId")
    private final Optional<Integer> threadId;
    @SerializedName("message")
    private final String message;
    @SerializedName("exception")
    private final Optional<String> exception;

    protected LogObject(Builder builder) {
        when = builder.when.orElseGet(Date::new);
        level = builder.level.orElseThrow(() -> new NullPointerException("no level provided"));
        levelId = builder.levelId.orElseThrow(() -> new NullPointerException("no levelId provided"));
        source = builder.source;
        logger = builder.logger.orElseThrow(() -> new NullPointerException("no logger provided"));
        threadId = builder.threadId;
        message = builder.message.orElseThrow(() -> new NullPointerException("no message provided"));
        exception = builder.exception;
    }

    public Date getWhen() {
        return when;
    }

    public String getLevel() {
        return level;
    }

    public int getLevelId() {
        return levelId;
    }

    public Optional<String> getSource() {
        return source;
    }

    public String getLogger() {
        return logger;
    }

    public Optional<Integer> getThreadId() {
        return threadId;
    }

    public String getMessage() {
        return message;
    }

    public Optional<String> getException() {
        return exception;
    }

    public static class Builder {

        private Optional<Date> when = empty();
        private Optional<String> level = empty();
        private Optional<Integer> levelId = empty();
        private Optional<String> source = empty();
        private Optional<String> logger = empty();
        private Optional<Integer> threadId = empty();
        private Optional<String> message = empty();
        private Optional<String> exception = empty();

        public Builder with(LogObject base) {
            withWhen(base.when);
            withLevel(base.level);
            withLevelId(base.levelId);
            withSource(base.source.orElse(null));
            withLogger(base.logger);
            withThreadId(base.threadId.orElse(null));
            withMessage(base.message);
            withException(base.exception.orElse(null));
            return this;
        }

        public Builder basedOn(LogRecord record) {
            withWhen(record.getMillis());
            withLevel(record.getLevel());
            withSource(record.getSourceClassName() + "." + record.getSourceMethodName());
            withLogger(record.getLoggerName());
            withThreadId(record.getThreadID());
            withMessage(INTERNAL_FORMATTER.formatMessage(record));
            withException(record.getThrown());
            return this;
        }

        public Builder withWhen(long when) {
            return withWhen(new Date(when));
        }

        public Builder withWhen(Date when) {
            this.when = of(requireNonNull(when, "when is null"));
            return this;
        }

        public Builder withLevel(Level level) {
            requireNonNull(level, "level is null");
            withLevel(level.getName());
            withLevelId(level.intValue());
            return this;
        }

        public Builder withLevel(String level) {
            this.level = of(requireNonNull(level, "level is null"));
            return this;
        }

        public Builder withLevelId(int levelId) {
            this.levelId = of(levelId);
            return this;
        }

        public Builder withSource(String source) {
            this.source = ofNullable(source);
            return this;
        }

        public Builder withLogger(String logger) {
            this.logger = of(requireNonNull(logger, "logger is null"));
            return this;
        }

        public Builder withThreadId(Integer threadId) {
            this.threadId = ofNullable(threadId);
            return this;
        }

        public Builder withMessage(String message) {
            this.message = of(requireNonNull(message, "message is null"));
            return this;
        }

        public Builder withException(Throwable exception) {
            return withException(ofNullable(exception)
                .map(t -> {
                    final StringWriter buf = new StringWriter();
                    t.printStackTrace(new PrintWriter(buf));
                    return buf.toString();
                })
                .orElse(null)
            );
        }

        public Builder withException(String exception) {
            this.exception = ofNullable(exception);
            return this;
        }

        public LogObject build() {
            return new LogObject(this);
        }

    }

}
