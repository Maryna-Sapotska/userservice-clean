package com.innowise.userservice.repository;

import com.innowise.userservice.model.entity.Card;
import org.springframework.data.jpa.domain.Specification;

public class CardSpecification {

    public static Specification<Card> hasHolder(String holder) {
        return (root, query, cb) -> {
            if (holder == null || holder.isBlank()) {
                return null;
            }

            return cb.like(
                    cb.lower(root.get("holder")),
                    "%" + holder.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Card> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) {
                return null;
            }

            return cb.equal(root.get("active"), active);
        };
    }

    public static Specification<Card> hasUserName(String userName) {
        return (root, query, cb) -> {
            if (userName == null || userName.isBlank()) {
                return null;
            }

            return cb.like(
                    cb.lower(root.join("user").get("name")),
                    "%" + userName.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Card> hasUserSurname(String userSurname) {
        return (root, query, cb) -> {
            if (userSurname == null || userSurname.isBlank()) {
                return null;
            }

            return cb.like(
                    cb.lower(root.join("user").get("surname")),
                    "%" + userSurname.toLowerCase() + "%"
            );
        };
    }
}
