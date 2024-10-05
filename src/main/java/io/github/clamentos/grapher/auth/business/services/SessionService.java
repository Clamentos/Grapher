package io.github.clamentos.grapher.auth.business.services;

///
import io.github.clamentos.grapher.auth.error.ErrorCode;
import io.github.clamentos.grapher.auth.error.ErrorFactory;

///..
import io.github.clamentos.grapher.auth.error.exceptions.AuthenticationException;
import io.github.clamentos.grapher.auth.error.exceptions.AuthorizationException;

///..
import io.github.clamentos.grapher.auth.persistence.UserRole;

///..
import io.github.clamentos.grapher.auth.persistence.entities.Session;

///..
import io.github.clamentos.grapher.auth.persistence.repositories.SessionRepository;

///.
import java.security.SecureRandom;

///..
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;

///..
import java.util.concurrent.ConcurrentHashMap;

///..
import java.util.concurrent.atomic.AtomicInteger;

///.
import lombok.extern.slf4j.Slf4j;

///.
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.core.env.Environment;

///..
import org.springframework.dao.DataAccessException;

///..
import org.springframework.scheduling.annotation.Scheduled;

///..
import org.springframework.stereotype.Service;

///..
import org.springframework.transaction.annotation.Transactional;

///
/**
 * <h3>Session Service</h3>
 * Spring {@link Service} to manage user {@link Session} objects.
*/

///
@Service
@Slf4j

///
public class SessionService {

    ///
    private final SessionRepository sessionRepository;

    ///..
    private final long sessionDuration;
    private final long sessionExpirationMargin;
    private final int maxSessionsPerUser;

    ///..
    private final SecureRandom secureRandom;
    private final Map<String, Session> sessions;
    private final Map<Long, AtomicInteger> userSessionCounters;

    ///
    /** This class is a Spring bean and this constructor should never be called explicitly. */
    @Autowired
    public SessionService(SessionRepository sessionRepository, Environment environment) {

        this.sessionRepository = sessionRepository;

        this.sessionDuration = environment.getProperty("grapher-auth.sessionDuration", Long.class, 3600000L);
        this.sessionExpirationMargin = environment.getProperty("grapher-auth.sessionExpirationMargin", Long.class, 5000L);
        this.maxSessionsPerUser = environment.getProperty("grapher-auth.maxSessionsPerUser", Integer.class, 1);

        secureRandom = new SecureRandom();
        sessions = new ConcurrentHashMap<>();
        userSessionCounters = new ConcurrentHashMap<>();

        for(Session session : sessionRepository.findAll()) {

            sessions.put(session.getId(), session);
            userSessionCounters.computeIfAbsent(session.getUserId(), key -> new AtomicInteger()).incrementAndGet();
        }
    }

    ///
    /**
     * Generates a new user session.
     * @param userId : The id of the target user.
     * @param username : The name of the target user.
     * @param role : The role of the target user.
     * @return The never {@code null} generated session.
     * @throws AuthorizationException If the target user has already reached the session count limit.
     * @throws DataAccessException If any database access error occurs.
    */
    @Transactional
    public Session generate(long userId, String username, UserRole role) throws AuthorizationException, DataAccessException {

        int count = userSessionCounters.computeIfAbsent(userId, k -> new AtomicInteger(1)).getAndUpdate(current -> {

            int attempt = current + 1;
            if(attempt <= maxSessionsPerUser) return(attempt);

            return(current);
        });

        if(count < maxSessionsPerUser) {

            byte[] rawSessionId = new byte[32];

            secureRandom.nextBytes(rawSessionId);
            String sessionId = Base64.getEncoder().encodeToString(rawSessionId);

            Session session = new Session(sessionId, userId, username, role, System.currentTimeMillis() + sessionDuration);

            sessionRepository.save(session);
            sessions.put(sessionId, session);

            return(session);
        }

        else {

            throw new AuthorizationException(ErrorFactory.create(

                ErrorCode.TOO_MANY_SESSIONS, 
                "SessionService::generate -> Could not create session, user has too many",
                username,
                maxSessionsPerUser
            ));
        }
    }

    ///..
    /**
     * Performs user authentication and authorization.
     * @param sessionId : The id of the target user session.
     * @param roles : The set of possible roles that the user can have (at least one, they are ORd).
     * @param minimumRequiredRole : The minimum role that the user must have to pass, {@code null} if none.
     * @param message : The optional message to put into the exception in case of authorization failures.
     * @return The never {@code null} validated matching user session.
     * @throws AuthenticationException If authentication fails, either because the session doesn't exist or is expired.
     * @throws AuthorizationException If authorization fails, that is, the user doesn't have enough privileges.
     * @throws NullPointerException If either {@code sessionId} or {@code roles} are {@code null}.
    */
    public Session check(String sessionId, Set<UserRole> roles, UserRole minimumRequiredRole, String message)
    throws AuthenticationException, AuthorizationException, NullPointerException {

        Session session = sessions.get(sessionId);

        if(session == null) {

            throw new AuthenticationException(ErrorFactory.create(

                ErrorCode.SESSION_NOT_FOUND, "SessionService::check -> Session not found"
            ));
        }

        if(session.getExpiresAt() - sessionExpirationMargin < System.currentTimeMillis()) {

            throw new AuthenticationException(ErrorFactory.create(

                ErrorCode.EXPIRED_SESSION, "SessionService::check -> Session expired", session.getExpiresAt(), sessionExpirationMargin
            ));
        }

        boolean hasNone = roles.size() > 0 && roles.contains(session.getUserRole()) == false;
        boolean failsMinimum = minimumRequiredRole != null && session.getUserRole().compareTo(minimumRequiredRole) < 0;

        if(hasNone || failsMinimum) {

            throw new AuthorizationException(ErrorFactory.create(

                ErrorCode.NOT_ENOUGH_PRIVILEGES,
                "SessionService::check -> " + message,
                minimumRequiredRole,
                roles
            ));
        }

        return(session);
    }

    ///..
    /**
     * Updates the role of all sessions for the specified user.
     * @param userId : The id of the target user.
     * @param role : The new role to apply.
     * @throws DataAccessException If any database access error occurs.
     * @apiNote If no sessions are found for the specified user, this method does nothing.
     * @apiNote This method ignores expired sessions.
    */
    @Transactional
    public void updateRole(long userId, UserRole role) throws DataAccessException {

        List<Session> updatedSessions = new ArrayList<>();
        List<UserRole> oldRoles = new ArrayList<>();
        long now = System.currentTimeMillis();

        for(Session session : sessions.values()) {

            if(session.getUserId() == userId && session.getExpiresAt() - sessionExpirationMargin >= now) {

                session.setUserRole(role);
                oldRoles.add(session.getUserRole());
                updatedSessions.add(session);
            }
        }

        try { sessionRepository.saveAll(updatedSessions); }

        catch(DataAccessException exc) {

            // The transaction was rolled-back, but the objects in the "updated" list still have the new role.
            // Put back the old roles and propagate the exception upwards.

            for(int i = 0; i < updatedSessions.size(); i++) updatedSessions.get(i).setUserRole(oldRoles.get(i));
            throw exc;
        }
    }

    ///..
    /**
     * Deletes the specified session.
     * @param sessionId : The id of the target user session.
     * @throws AuthenticationException If the session doesn't exist.
     * @throws DataAccessException If any database access error occurs.
     * @throws NullPointerException If {@code sessionId} is {@code null}.
    */
    @Transactional
    public void remove(String sessionId) throws AuthenticationException, DataAccessException, NullPointerException {

        Session toBeRemoved = sessions.get(sessionId);

        if(toBeRemoved == null) {

            throw new AuthenticationException(ErrorFactory.create(

                ErrorCode.SESSION_NOT_FOUND, "SessionService::remove -> Session not found"
            ));
        }

        sessionRepository.delete(toBeRemoved);
        Session removed = sessions.remove(sessionId);

        if(removed != null) {

            int count = userSessionCounters.get(toBeRemoved.getUserId()).decrementAndGet();
            if(count <= 0) userSessionCounters.remove(toBeRemoved.getUserId());
        }
    }

    ///..
    /**
     * Deletes all the user sessions that map to the specified user id.
     * @param userId : The id of the target user.
     * @apiNote If no sessions are found for the specified user, this method does nothing.
    */
    public void removeAll(long userId) {

        for(Session session : sessions.values()) {

            if(session.getUserId() == userId) {

                try { remove(session.getId()); }

                // The session was not found. It's ok, can happen if a session is removed from another method concurrently.
                catch(AuthenticationException exc) {}
                catch(DataAccessException exc) { log.error("Could not remove session because: {}, will skip this one", exc); }
            }
        }
    }

    ///.
    @Scheduled(fixedRate = 300000)
    private void removeAllExpired() {

        log.info("Starting expired session cleaning task...");
        long now = System.currentTimeMillis();

        for(Map.Entry<String, Session> session : sessions.entrySet()) {

            if(session.getValue().getExpiresAt() < now) {

                try { remove(session.getKey()); }

                catch(AuthenticationException exc) {} // The session was not found. It's ok, this is a cleaning method.
                catch(DataAccessException exc) { log.error("Could not remove expired session because: {}, will skip this one", exc); }
            }
        }

        log.info("Expired session cleaning task completed");
    }

    ///
}
