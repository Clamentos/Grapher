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
import io.github.clamentos.grapher.auth.business.mappers.UserMapper;

///..
import io.github.clamentos.grapher.auth.exceptions.AuthenticationException;
import io.github.clamentos.grapher.auth.exceptions.ValidationException;

///..
import io.github.clamentos.grapher.auth.persistence.entities.Audit;
import io.github.clamentos.grapher.auth.persistence.entities.User;

///..
import io.github.clamentos.grapher.auth.persistence.repositories.AuditRepository;
import io.github.clamentos.grapher.auth.persistence.repositories.UserRepository;

///..
import io.github.clamentos.grapher.auth.utility.Validator;

///..
import io.github.clamentos.grapher.auth.web.dtos.UserDetails;
import io.github.clamentos.grapher.auth.web.dtos.UsernamePassword;

///.
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

///.
import java.util.ArrayList;
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
    private final AuditRepository auditRepository;
    private final UserMapper mapper;
    private final KeyContext keyContext;
    private final long tokenDuration;

    ///
    @Autowired
    public UserService(

        UserRepository repository,
        AuditRepository auditRepository,
        UserMapper mapper,
        KeyContext keyContext,
        @Value("${grapher-auth.tokenDuration}") long tokenDuration
    ) {

        this.repository = repository;
        this.auditRepository = auditRepository;
        this.mapper = mapper;
        this.keyContext = keyContext;
        this.tokenDuration = tokenDuration;
    }

    ///
    public String login(UsernamePassword credentials) throws AuthenticationException, EntityNotFoundException, SecurityException {

        User user = repository.findByUsername(credentials.getUsername());

        if(user == null) {

            throw new EntityNotFoundException(

                "UserService.login -> " +
                "No user found with the username $\"" + credentials.getUsername() + "\"$."
            );
        }

        if(BCrypt.verifyer().verify(credentials.getPassword().toCharArray(), user.getPassword()).verified == false) {

            throw new AuthenticationException("UserService.login");
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

    ///..
    @Transactional
    public void register(UserDetails userDetails) {

        validate(userDetails);

        User user = mapper.map(userDetails);

        user.setId(ThreadLocalRandom.current().nextLong());
        user.setFlags((short)0);
        user.setOperations(new ArrayList<>());

        Audit audit = new Audit();

        audit.setTable("USER");
        audit.setColumns("id,username,password,email,flags");
        audit.setAction('C');
        audit.setCreatedAt(System.currentTimeMillis());
        audit.setCreatedBy("");

        repository.save(user);
        auditRepository.save(audit);
    }

    // read users
    // update user
    // delete user

    ///.
    private void validate(UserDetails userDetails) throws ValidationException {

        Validator.requireNull(userDetails.getId(), "id");
        Validator.requireFilled(userDetails.getUsername(), "username");
        Validator.requireFilled(userDetails.getPassword(), "password");
        Validator.requireNotNull(userDetails.getEmail(), "email");
        Validator.requireNull(userDetails.getFlags(), "flags");
        Validator.requireNull(userDetails.getOperations(), "operations");
    }

    ///
}
