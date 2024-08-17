package com.ssuamkiett.bookconnect.book;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {
    private static final String SHAREABLE = "shareable";
    private static final String ARCHIVED = "archived";
    public static Specification<Book> withOwnerId(Integer ownerId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("owner").get("id"), ownerId);
    }

    public static Specification<Book> hasKeywordInTitle(String keyword) {
        return (Root<Book> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            String[] keywords = keyword.split("\\s+");
            Predicate titlePredicate = builder.conjunction();
            for (String key : keywords) {
                titlePredicate = builder.and(titlePredicate, builder.like(builder.lower(root.get("title")), "%" + key.toLowerCase() + "%"));
            }
            return titlePredicate;
        };
    }

    public static Specification<Book> isNotArchived() {
        return (Root<Book> root, CriteriaQuery<?> query, CriteriaBuilder builder) ->
                builder.equal(root.get(ARCHIVED), false);
    }

    public static Specification<Book> isSharable() {
        return (Root<Book> root, CriteriaQuery<?> query, CriteriaBuilder builder) ->
                builder.equal(root.get(SHAREABLE), true);
    }
}
