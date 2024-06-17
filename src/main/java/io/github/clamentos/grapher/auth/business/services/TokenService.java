package io.github.clamentos.grapher.auth.business.services;

///
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;

///.
import io.github.clamentos.grapher.auth.business.contexts.KeyContext;

///.
import io.github.clamentos.grapher.auth.exceptions.AuthenticationException;
import io.github.clamentos.grapher.auth.exceptions.AuthorizationException;

///.
import io.github.clamentos.grapher.auth.persistence.entities.BlacklistedToken;

///.
import io.github.clamentos.grapher.auth.persistence.repositories.BlacklistedTokenRepository;

///.
import io.github.clamentos.grapher.auth.utility.ErrorCode;
import io.github.clamentos.grapher.auth.utility.ErrorFactory;

///.
import jakarta.transaction.Transactional;

///.
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

///..
import java.text.ParseException;

///..
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

///..
import java.util.concurrent.ConcurrentHashMap;

///.
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

///.
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.stereotype.Service;

///
@Service

///
public class TokenService {

    ///
    private final Logger logger;
    private final BlacklistedTokenRepository repository;
    private final KeyContext keyContext;

    ///..
    private final Map<String, Long> blacklist;

    ///
    @Autowired
    public TokenService(BlacklistedTokenRepository repository, KeyContext keyContext) {

        logger = LogManager.getLogger(this.getClass().getSimpleName());

        this.repository = repository;
        this.keyContext = keyContext;

        blacklist = new ConcurrentHashMap<>();
        List<BlacklistedToken> blacklistedTokens = repository.findAll();

        for(BlacklistedToken blacklistedToken : blacklistedTokens) {

            blacklist.put(blacklistedToken.getHash(), blacklistedToken.getExpiresAt());
        }
    }

    ///
    public String generate(Map<String, Object> claims) throws SecurityException {

        JWSObject jwsObject = new JWSObject(new JWSHeader.Builder(JWSAlgorithm.RS256).build(), new Payload(claims));

        try {

            jwsObject.sign(keyContext.getJwtSigner());
        }

        catch(JOSEException exc) {

            logger.error("Could not sign the JWT because: {}", exc);
            throw new SecurityException(ErrorFactory.generate(null));
        }

        return(jwsObject.serialize());
    }
    
    ///..
    public void authenticate(String token) throws AuthenticationException {

        if(token != null && token.length() > 0) {

            try {

                if(JWSObject.parse(token).verify(keyContext.getJwtVerifier()) == false) {

                    throw new AuthenticationException(ErrorFactory.generate(ErrorCode.INVALID_TOKEN));
                }

                if(blacklist.containsKey(stringify(MessageDigest.getInstance("SHA-256").digest(token.getBytes())))) {

                    throw new AuthenticationException(ErrorFactory.generate(ErrorCode.BLACKLISTED_TOKEN));
                }
            }

            catch(JOSEException | NoSuchAlgorithmException | ParseException exc) {

                logger.error("Could not verify the token: {}, because: {}", token, exc);
                throw new AuthenticationException(ErrorFactory.generate(ErrorCode.INVALID_TOKEN));
            }
        }

        else {

            throw new AuthenticationException(ErrorFactory.generate(ErrorCode.INVALID_TOKEN));
        }
    }

    ///..
    @SuppressWarnings("unchecked")
    public boolean[] authorize(String token, String... operations) throws AuthorizationException {

        try {

            List<String> userOps = (List<String>)JWSObject.parse(token).getPayload().toJSONObject().get("operations");

            if(userOps == null) {

                throw new AuthorizationException(ErrorFactory.generate(ErrorCode.NO_MATCHING_OPERATION));
            }

            boolean[] checked = new boolean[operations.length];
            Set<String> userOpsSet = new HashSet<>(userOps);

            for(int i = 0; i < operations.length; i++) {

                if(operations[i].charAt(0) == '#') {

                    checked[i] = userOpsSet.contains(operations[i]);
                }

                else {

                    if(userOpsSet.contains(operations[i]) == false) {

                        throw new AuthorizationException(ErrorFactory.generate(ErrorCode.NO_MATCHING_OPERATION));
                    }

                    checked[i] = true;
                }
            }

            return(checked);
        }

        catch(ParseException exc) {

            logger.error("Could not parse the token: {}, because: {}", token, exc);
            throw new AuthorizationException(ErrorFactory.generate(ErrorCode.INVALID_TOKEN));
        }
    }

    ///..
    @Transactional
    public void blacklistToken(String token) throws AuthenticationException {

        try {

            long expiration = (long)JWSObject.parse(token).getPayload().toJSONObject().get("exp");
            String hash = stringify(MessageDigest.getInstance("SHA-256").digest(token.getBytes()));

            repository.save(new BlacklistedToken(hash, expiration));
            blacklist.put(hash, expiration);

            // TODO: publish event to rabbit mq
        }

        catch(NoSuchAlgorithmException | ParseException exc) {

            logger.error("Could not blacklist the token: {}, because: {}", token, exc);
            throw new AuthenticationException(ErrorFactory.generate(ErrorCode.INVALID_TOKEN));
        }
    }

    ///.
    private String stringify(byte[] bytes) {

        StringBuilder hexString = new StringBuilder(2 * bytes.length);

        for (int i = 0; i < bytes.length; i++) {

            String hex = Integer.toHexString(bytes[i] & 0xFF);
            hexString.append(hex.length() == 1 ? '0' : hex);
        }

        return(hexString.toString());
    }

    ///
}
