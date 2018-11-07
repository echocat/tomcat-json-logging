package org.echocat.tjl;

import com.google.gson.annotations.SerializedName;
import org.apache.juli.OneLineFormatter;
import org.echocat.tjl.util.ExtendedLogRecord;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static java.lang.System.lineSeparator;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.*;
import static org.echocat.tjl.Constants.CONSOLE_DATE_FORMAT;

public class LogEvent {

    private final static Formatter INTERNAL_FORMATTER = new OneLineFormatter();

    public static Builder logObject() {
        return new Builder();
    }

    @SerializedName("when")
    private final Date when;
    @SerializedName("level")
    private final LogLevel level;
    @SerializedName("source")
    private final Optional<String> source;
    @SerializedName("logger")
    private final String logger;
    @SerializedName("thread")
    private final Optional<String> thread;
    @SerializedName("processId")
    private final Optional<Long> processId;
    @SerializedName("message")
    private final String message;
    @SerializedName("exception")
    private final Optional<String> exception;

    protected LogEvent(Builder builder) {
        when = builder.when.orElseGet(Date::new);
        level = builder.level.orElseThrow(() -> new NullPointerException("no level provided"));
        source = builder.source;
        logger = builder.logger.orElseThrow(() -> new NullPointerException("no logger provided"));
        thread = builder.thread;
        processId = builder.processId;
        message = builder.message.orElseThrow(() -> new NullPointerException("no message provided"));
        exception = builder.exception;
    }

    public Date getWhen() {
        return when;
    }

    public LogLevel getLevel() {
        return level;
    }

    public Optional<String> getSource() {
        return source;
    }

    public String getLogger() {
        return logger;
    }

    public Optional<String> getThread() {
        return thread;
    }

    public Optional<Long> getProcessId() {
        return processId;
    }

    public String getMessage() {
        return message;
    }

    public Optional<String> getException() {
        return exception;
    }

    public String formatAsText() {
        return String.format("%s %-5s %s --- [%15s] %-40s : %s%s",
            new SimpleDateFormat(CONSOLE_DATE_FORMAT).format(getWhen()),
            getLevel(),
            getProcessId().map(Object::toString).orElse(""),
            getThread().orElse(""),
            getLogger(),
            getMessage(),
            getException().map(val -> lineSeparator() + val).orElse("")
        );
    }

    @Override
    public String toString() {
        return formatAsText();
    }

    public static class Builder {

        private Optional<Date> when = empty();
        private Optional<LogLevel> level = empty();
        private Optional<String> source = empty();
        private Optional<String> logger = empty();
        private Optional<String> thread = empty();
        private Optional<Long> processId = empty();
        private Optional<String> message = empty();
        private Optional<String> exception = empty();

        public Builder with(LogEvent base) {
            withWhen(base.getWhen());
            withLevel(base.getLevel());
            withSource(base.getSource().orElse(null));
            withLogger(base.getLogger());
            withThread(base.getThread().orElse(null));
            withProcessId(base.getProcessId().orElse(null));
            withMessage(base.getMessage());
            withException(base.getException().orElse(null));
            return this;
        }

        public Builder basedOn(LogRecord record) {
            withWhen(record.getMillis());
            withLevel(record.getLevel());
            if (record instanceof ExtendedLogRecord) {
                withSource(((ExtendedLogRecord) record).getSource()
                    .orElse(null)
                );
                withThread(((ExtendedLogRecord) record).getThreadName()
                    .orElseGet(() -> Integer.toString(record.getThreadID()))
                );
                withProcessId(((ExtendedLogRecord) record).getProcessId()
                    .orElse(null)
                );
            } else {
                withSource(record.getSourceClassName() + "." + record.getSourceMethodName());
                withThread(Integer.toString(record.getThreadID()));
            }
            withLogger(record.getLoggerName());
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
            return withLevel(LogLevel.valueOf(level));
        }

        public Builder withLevel(LogLevel level) {
            this.level = of(requireNonNull(level, "level is null"));
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

        public Builder withThread(String thread) {
            this.thread = ofNullable(thread);
            return this;
        }

        public Builder withProcessId(Long processId) {
            this.processId = ofNullable(processId);
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

        public LogEvent build() {
            return new LogEvent(this);
        }

    }

}
