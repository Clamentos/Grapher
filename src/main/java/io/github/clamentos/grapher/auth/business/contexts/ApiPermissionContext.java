package io.github.clamentos.grapher.auth.business.contexts;

///
import io.github.clamentos.grapher.auth.messaging.Publisher;

///..
import io.github.clamentos.grapher.auth.persistence.entities.ApiPermission;

///..
import io.github.clamentos.grapher.auth.persistence.repositories.ApiPermissionRepository;

///..
import io.github.clamentos.grapher.auth.utility.Permission;

///.
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

///..
import java.util.concurrent.ConcurrentHashMap;

///.
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.stereotype.Component;

///
@Component

///
public final class ApiPermissionContext {

    ///
    private final ApiPermissionRepository repository;
    private final Publisher publisher;

    ///..
    private final Map<String, List<Permission>> apiPermissions;

    ///
    @Autowired
    public ApiPermissionContext(ApiPermissionRepository repository, Publisher publisher) {

        this.repository = repository;
        this.publisher = publisher;

        apiPermissions = new ConcurrentHashMap<>();
        apiPermissions.putAll(create(repository));
    }

    ///
    public List<Permission> getPermissions(String path) {

        return(apiPermissions.getOrDefault(path, List.of()));
    }

    ///..
    public void reload() {

        apiPermissions.putAll(create(repository));
        publisher.publishReloadEvent(apiPermissions);
    }

    ///.
    private static Map<String, List<Permission>> create(ApiPermissionRepository repository) {

        Map<String, List<Permission>> permissions = new ConcurrentHashMap<>();

        for(ApiPermission permission : repository.findAll()) {

            permissions

                .computeIfAbsent(permission.getPath(), key -> new ArrayList<>())
                .add(new Permission(permission.getOperation().getId(), permission.isOptional()))
            ;
        }

        return(permissions);
    }

    ///
}
