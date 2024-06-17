package io.github.clamentos.grapher.auth.utility;

///
import java.util.List;

///
public final class ErrorFactory {

    ///
    private static final String argumentSeparator = "|";
    private static final String errorCodeSeparator = ">";

    ///
    public static String generate(ErrorCode errorCode, Object... args) {

        StringBuilder builder = new StringBuilder(composePrefix(errorCode));

        for(Object arg : args) {

            builder.append(arg.toString()).append(argumentSeparator);
        }

        return(builder.toString());
    }

    ///..
    public static String generate(ErrorCode errorCode, List<Object> args) {

        StringBuilder builder = new StringBuilder(composePrefix(errorCode));

        for(Object arg : args) {

            builder.append(arg.toString()).append(argumentSeparator);
        }

        return(builder.toString());
    }

    ///.
    private static String composePrefix(ErrorCode errorCode) {

        if(errorCode == null) return("EC999" + errorCodeSeparator);
        return(errorCode.getValue() + errorCodeSeparator);
    }

    ///
}
