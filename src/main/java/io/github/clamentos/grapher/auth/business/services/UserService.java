package io.github.clamentos.grapher.auth.business.services;

///
import at.favre.lib.crypto.bcrypt.BCrypt;

///.
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;

///.
import io.github.clamentos.grapher.auth.business.contexts.KeyContext;

///..
import io.github.clamentos.grapher.auth.exceptions.LoginFailedException;

///..
import io.github.clamentos.grapher.auth.persistence.entities.User;

///..
import io.github.clamentos.grapher.auth.persistence.repositories.UserRepository;

///..
import io.github.clamentos.grapher.auth.web.dtos.LoginDetails;

///.
import jakarta.persistence.EntityNotFoundException;

///.
import java.util.HashMap;
import java.util.Map;

///..
import java.util.concurrent.ThreadLocalRandom;

///.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

///..
import org.springframework.stereotype.Service;

///
@Service

///
public final class UserService {

    ///
    private final UserRepository repository;
    private final KeyContext keyContext;
    private final long tokenDuration;

    ///
    @Autowired
    public UserService(UserRepository repository, KeyContext keyContext, @Value("${grapher-auth.tokenDuration}") long tokenDuration) {

        this.repository = repository;
        this.keyContext = keyContext;
        this.tokenDuration = tokenDuration;
    }

    ///
    public String login(LoginDetails details) throws EntityNotFoundException, LoginFailedException, SecurityException {

        User user = repository.findByUsername(details.getUsername());

        if(user == null) {

            throw new EntityNotFoundException(

                "UserService.login -> " +
                "No user found with the username $\"" + details.getUsername() + "\"$."
            );
        }

        if(BCrypt.verifyer().verify(details.getPassword().toCharArray(), user.getPassword()).verified == false) {

            throw new LoginFailedException("UserService.login");
        }

        Map<String, Object> claims = new HashMap<>();
        long now = System.currentTimeMillis();

        claims.put("jti", ThreadLocalRandom.current().nextLong());
        claims.put("iat", now);
        claims.put("exp", now + tokenDuration);
        claims.put("sub", user.getId());
        claims.put("name", user.getUsername());
        claims.put("flags", user.getFlags());
        claims.put("operations", user.getOperations());

        JWSObject jwsObject = new JWSObject(new JWSHeader.Builder(JWSAlgorithm.RS256).build(), new Payload(claims));

        try {

            jwsObject.sign(keyContext.getJwtSigner());
        }

        catch(JOSEException exc) {

            throw new SecurityException("UserService.login -> Could not sign the JWT.", exc);
        }

        return(jwsObject.serialize());
    }

    ///
}
