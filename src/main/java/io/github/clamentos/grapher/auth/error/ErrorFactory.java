package io.github.clamentos.grapher.auth.error;

///
/**
 * <h3>Error Factory</h3>
 * Static class dedicated to constructing parametrized exception detail messages.
*/

///
public final class ErrorFactory {

    ///
    // Forbids instantiation.
    private ErrorFactory() {}

    ///
    /**
     * <p>Constructs a parametrized exception details message with the following formatting:</p>
     * {@code <message>|<errorCode>|args[0]|args[1]|...}, the {@code |} (character represents the ASCII {@code 0x1}).
     * @param errorCode : The error code.
     * @param message : The extra message.
     * @param args : The message arguments.
     * @return The never {@code null} details message.
     * @throws NullPointerException If {@code args} is {@code null}.
    */
    public static String create(ErrorCode errorCode, String message, Object... args) throws NullPointerException {

        StringBuilder stringBuilder = new StringBuilder(message);
        stringBuilder.append("\1");

        if(errorCode == null) stringBuilder.append("EC999\1");
        else stringBuilder.append(errorCode.getValue()).append("\1");

        for(Object arg : args) stringBuilder.append(arg.toString()).append("\1");

        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return(stringBuilder.toString());
    }

    ///
}
