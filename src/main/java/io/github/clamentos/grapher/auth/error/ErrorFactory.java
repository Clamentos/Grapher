package io.github.clamentos.grapher.auth.error;

///
import io.github.clamentos.grapher.auth.utility.Constants;

///
public final class ErrorFactory {

    ///
    public static String generate(ErrorCode errorCode, String... args) {

        StringBuilder builder = new StringBuilder(composePrefix(errorCode));

        for(String arg : args) {

            builder.append(arg).append(Constants.ERROR_ARG_SEPARATOR);
        }

        builder.deleteCharAt(builder.length() - 1);
        return(builder.toString());
    }

    ///..
    public static String generate(ErrorCode errorCode, Iterable<?> args) {

        StringBuilder builder = new StringBuilder(composePrefix(errorCode));

        for(Object arg : args) {

            builder.append(arg.toString()).append(Constants.ERROR_ARG_SEPARATOR);
        }

        builder.deleteCharAt(builder.length() - 1);
        return(builder.toString());
    }

    ///.
    private static String composePrefix(ErrorCode errorCode) {

        if(errorCode == null) return("EC999" + Constants.ERROR_CODE_SEPARATOR);
        return(errorCode.getValue() + Constants.ERROR_CODE_SEPARATOR);
    }

    ///
}
