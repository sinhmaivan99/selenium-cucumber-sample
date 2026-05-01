package utils;

import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Centralized logging utility with Allure integration.
 * Uses StackWalker to automatically resolve the calling class name,
 * so log output shows the actual caller instead of "LogHelper".
 */
public final class LogHelper {

    private LogHelper() {
        // Utility class — prevent instantiation
    }

    private static Logger getLogger() {
        return LogManager.getLogger(
                StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                        .getCallerClass());
    }

    @Step("{message}")
    public static void info(String message) {
        getLogger().info(message);
    }

    public static void warn(String message) {
        getLogger().warn(message);
    }

    public static void error(String message) {
        getLogger().error(message);
    }

    public static void error(String message, Throwable t) {
        getLogger().error(message, t);
    }

    public static void debug(String message) {
        getLogger().debug(message);
    }
}
