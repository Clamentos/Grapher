package io.github.clamentos.grapher.auth.persistence.specifications;

///
import io.github.clamentos.grapher.auth.persistence.entities.Operation;
import io.github.clamentos.grapher.auth.persistence.entities.User;

///.
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

///.
import java.util.ArrayList;
import java.util.List;

///.
import lombok.AllArgsConstructor;

///.
import org.springframework.data.jpa.domain.Specification;

///
@AllArgsConstructor

///
public final class UserSpecification implements Specification<User> {

    ///
    private final String username;
    private final String email;
    private final Long createdAtStart;
    private final Long createdAtEnd;
    private final Long updatedAtStart;
    private final Long updatedAtEnd;
    private final String[] operations;

    ///
    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        List<Predicate> predicates = new ArrayList<>();

        if(username.equals("") == false) predicates.add(builder.like(root.get("username"), username + "%"));
        if(email.equals("") == false) predicates.add(builder.like(root.get("email"), email + "%"));
        if(createdAtStart != null) predicates.add(builder.greaterThanOrEqualTo(root.get("createdAt"), createdAtStart));
        if(createdAtEnd != null) predicates.add(builder.lessThanOrEqualTo(root.get("createdAt"), createdAtEnd));
        if(updatedAtStart != null) predicates.add(builder.greaterThanOrEqualTo(root.get("updatedAt"), updatedAtStart));
        if(updatedAtEnd != null) predicates.add(builder.lessThanOrEqualTo(root.get("updatedAt"), updatedAtEnd));

        if(operations != null && operations.length > 0) {

            Join<User, Operation> userOperationJoin = root.join("operations").join("operation");
            predicates.add(builder.in(userOperationJoin.get("name")).value(operations));
        }

        return(builder.and(predicates.toArray(new Predicate[0])));
    }

    ///
}
