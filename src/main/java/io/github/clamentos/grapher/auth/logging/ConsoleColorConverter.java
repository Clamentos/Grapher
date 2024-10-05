package io.github.clamentos.grapher.auth.logging;

///
import ch.qos.logback.classic.Level;

///..
import ch.qos.logback.classic.spi.ILoggingEvent;

///..
import ch.qos.logback.core.pattern.color.ANSIConstants;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;

///
/**
 * <h3>Console Color Converter</h3>
 * Custom Logback {@link ForegroundCompositeConverterBase} for {@link ILoggingEvent} to apply custom console coloring.
*/

///
public final class ConsoleColorConverter extends ForegroundCompositeConverterBase<ILoggingEvent> {

    ///
    /** Instantiates a new {@link ConsoleColorConverter} object. */
    public ConsoleColorConverter() { super(); }

    ///
    /**
     * Associates each log level its corresponding console color.
     * @param event : The console log event.
     * @return The never {@code null} ANSI color escape sequence.
     * @throws NullPointerException If {@code event} or its parameter {@link ILoggingEvent#level} is {@code null}.
    */
    @Override
    protected String getForegroundColorCode(ILoggingEvent event) throws NullPointerException {

        return switch(event.getLevel().toInt()) {

            case Level.ERROR_INT -> ANSIConstants.BOLD + ANSIConstants.RED_FG;
            case Level.WARN_INT -> ANSIConstants.BOLD + ANSIConstants.YELLOW_FG;
            case Level.INFO_INT -> ANSIConstants.BOLD + ANSIConstants.GREEN_FG;
            case Level.DEBUG_INT -> ANSIConstants.BOLD + ANSIConstants.CYAN_FG;
            case Level.TRACE_INT -> ANSIConstants.BOLD + ANSIConstants.WHITE_FG;

            default -> ANSIConstants.BOLD + ANSIConstants.DEFAULT_FG;
        };
    }

    ///
}
