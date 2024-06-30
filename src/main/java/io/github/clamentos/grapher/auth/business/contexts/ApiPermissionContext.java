package io.github.clamentos.grapher.auth.business.contexts;

///
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
    private final Map<String, List<Permission>> apiPermissions;

    ///
    @Autowired
    public ApiPermissionContext(ApiPermissionRepository repository) {

        this.repository = repository;
        apiPermissions = new ConcurrentHashMap<>();

        fill(apiPermissions, repository, false);
    }

    ///
    public List<Permission> getPermissions(String path) {

        return(apiPermissions.get(path));
    }

    ///..
    public void reload() {

        fill(apiPermissions, repository, true);
        // TODO: publist event to rabbitmq saying that all services must "pull" the new api permission list
    }

    ///.
    private static void fill(Map<String, List<Permission>> apiPermissions, ApiPermissionRepository repository, boolean overwrite) {

        // TODO: overwrite

        for(ApiPermission permission : repository.findAll()) {

            apiPermissions.compute(permission.getPath(), (k, v) -> {

                List<Permission> values = v != null ? v : new ArrayList<>();
                values.add(new Permission(permission.getOperation().getId(), permission.isOptional()));

                return(values);
            });
        }
    }

    ///
}
