package io.github.clamentos.grapher.auth.business.services;

///
import io.github.clamentos.grapher.auth.business.contexts.ApiPermissionContext;

///..
import io.github.clamentos.grapher.auth.error.ErrorCode;
import io.github.clamentos.grapher.auth.error.ErrorFactory;

///..
import io.github.clamentos.grapher.auth.error.exceptions.AuthenticationException;
import io.github.clamentos.grapher.auth.error.exceptions.AuthorizationException;
import io.github.clamentos.grapher.auth.error.exceptions.SessionNotFoundException;
import io.github.clamentos.grapher.auth.error.exceptions.TooManySessionsException;

///..
import io.github.clamentos.grapher.auth.messaging.Publisher;

///..
import io.github.clamentos.grapher.auth.persistence.entities.Session;

///..
import io.github.clamentos.grapher.auth.persistence.repositories.SessionRepository;

///..
import io.github.clamentos.grapher.auth.utility.Permission;
import io.github.clamentos.grapher.auth.utility.UserSession;

///.
import java.security.SecureRandom;

///..
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;

///..
import java.util.concurrent.ConcurrentHashMap;

///..
import java.util.concurrent.atomic.AtomicInteger;

///..
import java.util.stream.Collectors;

///.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

///..
import org.springframework.stereotype.Service;

///..
import org.springframework.transaction.annotation.Transactional;

///
@Service

///
public class SessionService {

    ///
    private final SessionRepository repository;
    private final ApiPermissionContext apiPermissionContext;
    private final Publisher publisher;

    ///..
    private final long sessionDuration;
    private final int maxSessionsPerUser;

    ///..
    private final SecureRandom secureRandom;
    private final Map<String, UserSession> sessions;
    private final Map<Long, AtomicInteger> counters;

    ///
    @Autowired
    public SessionService(

        SessionRepository repository,
        ApiPermissionContext apiPermissionContext,
        Publisher publisher,

        @Value("${grapher-auth.sessionDuration}") long sessionDuration,
        @Value("${grapher-auth.maxSessionsPerUser}") int maxSessionsPerUser
    ) {

        this.repository = repository;
        this.apiPermissionContext = apiPermissionContext;
        this.publisher = publisher;

        this.sessionDuration = sessionDuration;
        this.maxSessionsPerUser = maxSessionsPerUser;

        secureRandom = new SecureRandom();
        sessions = new ConcurrentHashMap<>();
        counters = new ConcurrentHashMap<>();

        long now = System.currentTimeMillis();

        for(Session session : repository.findAll()) {

            sessions.put(session.getSessionId(), new UserSession(

                session.getUserId(),
                session.getUsername(),
                Arrays.stream(session.getOperationIds().split(",")).map(Short::parseShort).collect(Collectors.toSet()),
                now + sessionDuration
            ));

            counters.computeIfAbsent(session.getUserId(), key -> new AtomicInteger()).incrementAndGet();
        }
    }

    ///
    @Transactional
    public String generate(long userId, String username, Set<Short> operationIds) throws IllegalArgumentException, SecurityException {

        if(username != null && username.length() > 0 && operationIds != null) {

            byte[] rawSessionId = new byte[32];
            long expiration = System.currentTimeMillis() + sessionDuration;

            secureRandom.nextBytes(rawSessionId);
            String sessionId = Base64.getEncoder().encodeToString(rawSessionId);

            repository.save(new Session(

                sessionId,
                userId,
                username,
                operationIds.stream().map(e -> e.toString()).collect(Collectors.joining(",")),
                expiration
            ));

            // FIXME:
            int count = counters.computeIfAbsent(userId, k -> new AtomicInteger(1)).getAndAccumulate(1, (current, x) -> {

                int attempt = current + x;

                if(attempt <= maxSessionsPerUser) return(attempt);
                return(current);
            });

            if(count < maxSessionsPerUser) {

                UserSession session = new UserSession(userId, username, operationIds, expiration);

                sessions.put(sessionId, session);
                publisher.publishSessionGenerated(sessionId, session);

                return(sessionId);
            }

            else {

                throw new TooManySessionsException(ErrorFactory.generate(ErrorCode.TOO_MANY_SESSIONS, username));
            }
        }

        else {

            throw new IllegalArgumentException();
        }
    }

    ///..
    public UserSession getUserSession(String sessionId) throws IllegalArgumentException, SessionNotFoundException {

        if(sessionId != null) {

            UserSession session = sessions.get(sessionId);

            if(session != null) return(session);
            throw new SessionNotFoundException(ErrorFactory.generate(ErrorCode.SESSION_NOT_FOUND));
        }

        else {

            throw new IllegalArgumentException();
        }
    }

    ///..
    public void authenticate(String sessionId) throws AuthenticationException, IllegalArgumentException {

        if(sessionId != null) {

            UserSession session = sessions.get(sessionId);

            if(session != null) {

                if(session.getExpiresAt() < System.currentTimeMillis()) {

                    throw new AuthenticationException(ErrorFactory.generate(ErrorCode.EXPIRED_SESSION));
                }
            }

            else {

                throw new AuthenticationException(ErrorFactory.generate(ErrorCode.SESSION_NOT_FOUND));
            }
        }

        else {

            throw new IllegalArgumentException();
        }
    }

    ///..
    public boolean[] authorize(String sessionId, String path)
    throws AuthenticationException, AuthorizationException, IllegalArgumentException {

        if(sessionId != null && path != null) {

            UserSession session = sessions.get(sessionId);

            if(session != null) {

                Set<Short> userOperationIds = session.getOperationIds();

                if(userOperationIds.size() > 0) {

                    List<Permission> permissions = apiPermissionContext.getPermissions(path);
                    boolean[] checked = new boolean[permissions.size()];

                    for(int i = 0; i < permissions.size(); i++) {

                        if(userOperationIds.contains(permissions.get(i).getOperationId())) {

                            checked[i] = true;
                        }

                        else {

                            if(permissions.get(i).isOptional()) checked[i] = false;
                            else throw new AuthorizationException(ErrorFactory.generate(ErrorCode.NOT_ENOUGH_PRIVILEGES));
                        }
                    }

                    return(checked);
                }

                else {

                    throw new AuthorizationException(ErrorFactory.generate(ErrorCode.NOT_ENOUGH_PRIVILEGES));
                }
            }

            else {

                throw new AuthenticationException(ErrorFactory.generate(ErrorCode.SESSION_NOT_FOUND));
            }
        }

        else {

            throw new IllegalArgumentException();
        }
    }

    ///..
    @Transactional
    public void remove(String sessionId) throws IllegalArgumentException {

        if(sessionId != null) {

            UserSession toBeRemoved = sessions.get(sessionId);

            if(toBeRemoved != null) {

                repository.deleteById(sessionId);
                sessions.remove(sessionId);
                counters.get(toBeRemoved.getUserId()).decrementAndGet();
                publisher.publishSessionRemoved(sessionId);
            }
        }

        else {

            throw new IllegalArgumentException();
        }
    }

    ///..
    public void removeExpired() {

        long now = System.currentTimeMillis();

        for(Map.Entry<String, UserSession> entry : sessions.entrySet()) {

            if(entry.getValue().getExpiresAt() < now) remove(entry.getKey());
        }
    }

    ///
}
