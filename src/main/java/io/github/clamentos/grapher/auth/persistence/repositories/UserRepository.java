package io.github.clamentos.grapher.auth.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.github.clamentos.grapher.auth.persistence.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(

        value = "select * " +
                "from USER u JOIN USER_OPERATION uo ON (u.id = uo.user_id) JOIN OPERATION o ON (uo.operation_id = o.id) " +
                "where username = ?1",
        nativeQuery = true
    )
    User findByUsername(String username);
}
