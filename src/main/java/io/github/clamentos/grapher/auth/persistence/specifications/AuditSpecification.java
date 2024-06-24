package io.github.clamentos.grapher.auth.persistence.specifications;

///
import io.github.clamentos.grapher.auth.persistence.entities.Audit;

///.
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
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
public final class AuditSpecification implements Specification<Audit> {

    ///
    private final Long recordId;
    private final String tableName;
    private final Character action;
    private final Long createdAtStart;
    private final Long createdAtEnd;
    private final Long updatedAtStart;
    private final Long updatedAtEnd;

    ///
    @Override
    public Predicate toPredicate(Root<Audit> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        List<Predicate> predicates = new ArrayList<>();

        if(recordId != null) predicates.add(builder.equal(root.get("recordId"), recordId));
        if(tableName != null) predicates.add(builder.equal(root.get("tableName"), tableName));
        if(action != null) predicates.add(builder.equal(root.get("action"), action));
        if(createdAtStart != null) predicates.add(builder.greaterThanOrEqualTo(root.get("createdAt"), createdAtStart));
        if(createdAtEnd != null) predicates.add(builder.lessThanOrEqualTo(root.get("createdAt"), createdAtEnd));
        if(updatedAtStart != null) predicates.add(builder.greaterThanOrEqualTo(root.get("updatedAt"), updatedAtStart));
        if(updatedAtEnd != null) predicates.add(builder.lessThanOrEqualTo(root.get("updatedAt"), updatedAtEnd));

        return(builder.and(predicates.toArray(new Predicate[0])));
    }

    ///
}
