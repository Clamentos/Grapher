package io.github.clamentos.grapher.auth.error;

///
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

///
/**
 * <h3>Error Factory</h3>
 * Static class dedicated to constructing parametrized exception detail messages.
*/

///
@NoArgsConstructor(access = AccessLevel.PRIVATE)

///
public final class ErrorFactory {

    ///
    /**
     * <p>Constructs a parametrized exception details message with the following formatting:</p>
     * {@code <errorCode>/<message>/args[0]/args[1]/...}.
     * @param errorCode : The error code.
     * @param message : The extra message.
     * @param args : The message arguments.
     * @return The never {@code null} details message.
     * @throws NullPointerException If {@code args} is {@code null}.
    */
    public static String create(ErrorCode errorCode, String message, Object... args) throws NullPointerException {

        StringBuilder stringBuilder = new StringBuilder(errorCode != null ? errorCode.name() : ErrorCode.getDefault().name());

        stringBuilder.append("/");
        stringBuilder.append(message);
        stringBuilder.append("/");

        for(Object arg : args) {

            stringBuilder.append(arg).append("/");
        }

        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return(stringBuilder.toString());
    }

    ///
}
