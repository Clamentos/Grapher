package io.github.clamentos.grapher.auth.business.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;

import io.github.clamentos.grapher.auth.business.contexts.KeyContext;
import io.github.clamentos.grapher.auth.exceptions.AuthenticationException;
import io.github.clamentos.grapher.auth.persistence.entities.BlacklistedToken;
import io.github.clamentos.grapher.auth.persistence.repositories.BlacklistedTokenRepository;

@Service
public final class TokenService {

    private final BlacklistedTokenRepository repository;
    private final KeyContext keyContext;
    private final Map<String, Long> blacklist;

    @Autowired
    public TokenService(BlacklistedTokenRepository repository, KeyContext keyContext) {

        this.repository = repository;
        this.keyContext = keyContext;

        blacklist = new ConcurrentHashMap<>();

        List<BlacklistedToken> blacklistedTokens = repository.findAll();

        for(BlacklistedToken blacklistedToken : blacklistedTokens) {

            blacklist.put(blacklistedToken.getHash(), blacklistedToken.getCreatedAt());
        }
    }

    public void verifyToken(String token) throws AuthenticationException {

        if(token != null && token.length() > 0) {

            try {

                if(JWSObject.parse(token).verify(keyContext.getJwtVerifier()) == false) {

                    throw new AuthenticationException("TokenService.verifyToken -> Could not verify the token.");
                }

                String hash = stringify(MessageDigest.getInstance("SHA-256").digest(token.getBytes()));

                if(blacklist.containsKey(hash)) {

                    throw new AuthenticationException("TokenService.verifyToken -> Token: " + hash + " is blacklisted.");
                }
            }

            catch(JOSEException | NoSuchAlgorithmException | ParseException exc) {

                if(exc instanceof JOSEException) {

                    throw new AuthenticationException("TokenService.verifyToken -> Could not verify the token.", exc);
                }

                if(exc instanceof ParseException) {

                    throw new AuthenticationException("TokenService.verifyToken -> Could not parse the token.", exc);
                }
            }
        }

        else {

            throw new AuthenticationException("TokenService.verifyToken -> Null or empty token.");
        }
    }

    public void blacklistToken(String token) {

        // TODO: insert into blacklist & db & publish event to rabbit mq
    }

    private String stringify(byte[] bytes) {

        StringBuilder hexString = new StringBuilder(2 * bytes.length);

        for (int i = 0; i < bytes.length; i++) {

            String hex = Integer.toHexString(bytes[i] & 0xFF);
            hexString.append(hex.length() == 1 ? '0' : hex);
        }

        return(hexString.toString());
    }
}
