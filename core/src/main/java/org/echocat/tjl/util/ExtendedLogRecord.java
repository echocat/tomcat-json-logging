package org.echocat.tjl.util;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.LogRecord;

import static java.lang.Long.parseLong;
import static java.lang.Thread.currentThread;
import static java.lang.management.ManagementFactory.getRuntimeMXBean;
import static java.util.Arrays.stream;
import static java.util.Optional.*;

public class ExtendedLogRecord extends LogRecord {

    protected static final Optional<Long> CURRENT_PROCESS_ID = findCurrentProcessId();

    private Optional<String> sourceFileName;
    private Optional<Integer> sourceLineNumber;
    private Optional<String> threadName;
    private Optional<Long> processId;

    public ExtendedLogRecord(LogRecord source) {
        super(source.getLevel(), source.getMessage());
        setLoggerName(source.getLoggerName());
        setParameters(source.getParameters());
        setResourceBundle(source.getResourceBundle());
        setResourceBundleName(source.getResourceBundleName());
        setSequenceNumber(source.getSequenceNumber());
        setSourceClassName(source.getSourceClassName());
        setSourceMethodName(source.getSourceMethodName());
        setThreadID(source.getThreadID());
        setThrown(source.getThrown());
        //noinspection deprecation
        setMillis(source.getMillis());
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = ofNullable(sourceFileName);
    }

    public void setSourceLineNumber(Integer sourceLineNumber) {
        this.sourceLineNumber = ofNullable(sourceLineNumber);
    }

    public void setThreadName(String threadName) {
        this.threadName = ofNullable(threadName);
    }

    public void setProcessId(Long processId) {
        this.processId = ofNullable(processId);
    }

    public Optional<String> getSourceFileName() {
        return sourceFileName;
    }

    public Optional<Integer> getSourceLineNumber() {
        return sourceLineNumber;
    }

    public Optional<String> getThreadName() {
        return threadName;
    }

    public Optional<Long> getProcessId() {
        return processId;
    }

    public Optional<String> getSource() {
        final StringBuilder sb = new StringBuilder();

        ofNullable(getSourceClassName()).ifPresent(sb::append);

        ofNullable(getSourceMethodName()).ifPresent(methodName -> {
            if (sb.length() > 0) {
                sb.append('.');
            }
            sb.append(methodName);
        });

        getSourceFileName().ifPresent(fileName -> {
            final boolean inBrackets = sb.length() > 0;
            if (inBrackets) {
                sb.append('(');
            }
            sb.append(fileName);
            getSourceLineNumber().ifPresent(line ->
                sb.append(':').append(line)
            );
            if (inBrackets) {
                sb.append(')');
            }
        });

        return of(sb.toString())
            .filter(str -> !str.isEmpty());
    }

    public void detectAndSetSource(Predicate<String> acceptableClassName) {
        final Thread thread = currentThread();
        stream(thread.getStackTrace())
            .skip(1)
            .filter(element -> isAcceptableCallerClass(element.getClassName()))
            .filter(element -> acceptableClassName.test(element.getClassName()))
            .findFirst()
            .ifPresent(element -> {
                setSourceClassName(element.getClassName());
                setSourceMethodName(element.getMethodName());
                setSourceFileName(element.getFileName());
                setSourceLineNumber(element.getLineNumber());
            });
        setThreadName(thread.getName());
        setProcessId(CURRENT_PROCESS_ID.orElse(null));
    }

    protected static Optional<Long> findCurrentProcessId() {
        final String name = getRuntimeMXBean().getName();
        final String firstNumber = name.replaceFirst("^.*?(\\d+).*$", "$1");
        try {
            return of(parseLong(firstNumber));
        } catch (NumberFormatException ignored) {
            return empty();
        }
    }

    protected boolean isAcceptableCallerClass(String candidate) {
        return !candidate.startsWith("java.util.logging.")
            && !candidate.startsWith("org.apache.juli.logging")
            && !candidate.equals("org.echocat.tjl.util.ExtendedLogRecord")
            ;
    }

}
