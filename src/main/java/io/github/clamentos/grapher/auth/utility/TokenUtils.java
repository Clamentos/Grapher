package io.github.clamentos.grapher.auth.utility;

///
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;

///.
import io.github.clamentos.grapher.auth.exceptions.AuthenticationException;

///.
import java.text.ParseException;

///..
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

///
public final class TokenUtils {

    ///
    public static List<Object> getClaims(String token, String... claimNames) throws AuthenticationException {

        try {

            Payload payload = JWSObject.parse(token).getPayload();

            if(payload != null) {

                List<Object> claims = new ArrayList<>();
                Map<String, Object> payloadMap = payload.toJSONObject();

                for(String claimName : claimNames) {

                    Object claim = payloadMap.get(claimName);

                    if(claim != null) {

                        claims.add(claim);
                    }

                    else {

                        throw new AuthenticationException(ErrorFactory.generate(ErrorCode.INVALID_TOKEN));
                    }
                }

                return(claims);
            }

            else {

                throw new AuthenticationException(ErrorFactory.generate(ErrorCode.INVALID_TOKEN));
            }
        }

        catch(ParseException exc) {

            throw new AuthenticationException(ErrorFactory.generate(ErrorCode.INVALID_TOKEN));
        }
    }

    ///
}
