package com.innowise.userservice.repository;

import com.innowise.userservice.model.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {

    List<Card> findByUser_Id(Long userId);

    @Query("""
            SELECT c
            FROM Card c
            WHERE c.active = true
            """)
    List<Card> findAllActiveCards();

    @Query(value = """
            SELECT *
            FROM payment_cards
            WHERE active = true
            """,
            nativeQuery = true)
    List<Card> findAllActiveCardsNative();

    @Query("""
    SELECT COUNT(c)
    FROM Card c
    WHERE c.user.id = :userId
""")
    long countUserCards(@Param("userId") Long userId);
}
