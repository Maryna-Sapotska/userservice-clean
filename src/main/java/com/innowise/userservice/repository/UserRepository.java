package com.innowise.userservice.repository;

import com.innowise.userservice.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    @Query("""
            SELECT u
            FROM User u
            WHERE u.active = true
            """)
    List<User> findAllActiveUsers();

    @Query(value = """
            SELECT *
            FROM users
            WHERE surname = :surname
            """,
            nativeQuery = true)
    List<User> findBySurnameNative(String surname);

    @Query("""
    SELECT u FROM User u
    LEFT JOIN FETCH u.cards
    WHERE u.id = :id
""")
    Optional<User> findByIdWithCards(@Param("id") Long id);
}
