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
}
