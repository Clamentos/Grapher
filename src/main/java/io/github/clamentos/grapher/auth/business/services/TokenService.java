package io.github.clamentos.grapher.auth.business.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;

import io.github.clamentos.grapher.auth.business.contexts.KeyContext;
import io.github.clamentos.grapher.auth.persistence.entities.BlacklistedToken;
import io.github.clamentos.grapher.auth.persistence.repositories.BlacklistedTokenRepository;

@Service
public final class TokenService {

    private final Logger logger;
    private final BlacklistedTokenRepository repository;
    private final KeyContext keyContext;
    private final Map<String, Long> blacklist;

    @Autowired
    public TokenService(BlacklistedTokenRepository repository, KeyContext keyContext) {

        this.repository = repository;
        this.keyContext = keyContext;

        logger = LogManager.getLogger(this.getClass().getSimpleName());
        blacklist = new ConcurrentHashMap<>();

        List<BlacklistedToken> blacklistedTokens = repository.findAll();

        for(BlacklistedToken blacklistedToken : blacklistedTokens) {

            blacklist.put(blacklistedToken.getHash(), blacklistedToken.getCreatedAt());
        }
    }

    public boolean isValid(String token) {

        if(token != null && token.length() > 0) {

            try {

                if(JWSObject.parse(token).verify(keyContext.getJwtVerifier()) == true) {

                    String hash = stringify(MessageDigest.getInstance("SHA-256").digest(token.getBytes()));
                    return(blacklist.containsKey(hash) == false);
                }
            }

            catch(JOSEException | NoSuchAlgorithmException | ParseException exc) {

                if(exc instanceof JOSEException) {

                    logger.warn("Could not determine token validity.", exc);
                }
            }
        }

        return(false);
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
