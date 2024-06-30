package io.github.clamentos.grapher.auth.business.services;

///
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;

///.
import io.github.clamentos.grapher.auth.business.contexts.ApiPermissionContext;
import io.github.clamentos.grapher.auth.business.contexts.KeyContext;

///..
import io.github.clamentos.grapher.auth.error.ErrorCode;
import io.github.clamentos.grapher.auth.error.ErrorFactory;

///..
import io.github.clamentos.grapher.auth.error.exceptions.AuthenticationException;
import io.github.clamentos.grapher.auth.error.exceptions.AuthorizationException;

///..
import io.github.clamentos.grapher.auth.persistence.entities.BlacklistedToken;

///..
import io.github.clamentos.grapher.auth.persistence.repositories.BlacklistedTokenRepository;

///..
import io.github.clamentos.grapher.auth.utility.Permission;

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
    private final ApiPermissionContext apiPermissionContext;

    ///..
    private final Map<String, Long> blacklist;

    ///
    @Autowired
    public TokenService(BlacklistedTokenRepository repository, KeyContext keyContext, ApiPermissionContext apiPermissionContext) {

        logger = LogManager.getLogger(this.getClass().getSimpleName());

        this.repository = repository;
        this.keyContext = keyContext;
        this.apiPermissionContext = apiPermissionContext;

        blacklist = new ConcurrentHashMap<>();

        for(BlacklistedToken blacklistedToken : repository.findAll()) {

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
    public boolean[] authorize(String token, String path) throws AuthorizationException {

        try {

            Set<Long> userOpIds = new HashSet<>((List<Long>)JWSObject.parse(token).getPayload().toJSONObject().get("operations"));

            if(userOpIds.size() == 0) {

                throw new AuthorizationException(ErrorFactory.generate(ErrorCode.NOT_ENOUGH_PRIVILEGES));
            }

            List<Permission> permissions = apiPermissionContext.getPermissions(path);

            if(permissions == null) {

                throw new AuthorizationException(ErrorFactory.generate(ErrorCode.ILLEGAL_ACTION));
            }

            boolean[] checked = new boolean[permissions.size()];

            for(int i = 0; i < permissions.size(); i++) {

                if(userOpIds.contains(Long.valueOf(permissions.get(i).getOperationId()))) {

                    checked[i] = true;
                }

                else {

                    if(permissions.get(i).isOptional() == false) {

                        throw new AuthorizationException(ErrorFactory.generate(ErrorCode.NOT_ENOUGH_PRIVILEGES));
                    }

                    checked[i] = false;
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

            // TODO: publish event to rabbit mq to update other caches
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
