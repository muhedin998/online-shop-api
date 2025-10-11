package com.example.online_shop.product.specification;

import com.example.online_shop.product.dto.ProductSearchCriteria;
import com.example.online_shop.product.model.Product;
import com.example.online_shop.product.model.ProductCategory;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> withCriteria(ProductSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Text-based search (case-insensitive) across name and description
            if (criteria.getSearchText() != null && !criteria.getSearchText().trim().isEmpty()) {
                // Sanitize search text to prevent SQL wildcard issues
                String sanitizedText = criteria.getSearchText()
                        .trim()
                        .toLowerCase()
                        .replace("\\", "\\\\")  // Escape backslash
                        .replace("%", "\\%")    // Escape SQL wildcard
                        .replace("_", "\\_");   // Escape SQL wildcard

                String searchPattern = "%" + sanitizedText + "%";
                Predicate namePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        searchPattern,
                        '\\'  // Escape character
                );
                Predicate descriptionPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")),
                        searchPattern,
                        '\\'  // Escape character
                );
                predicates.add(criteriaBuilder.or(namePredicate, descriptionPredicate));
            }

            // Filter by category
            if (criteria.getCategoryId() != null) {
                Join<Product, ProductCategory> categoryJoin = root.join("category", JoinType.LEFT);
                predicates.add(criteriaBuilder.equal(categoryJoin.get("id"), criteria.getCategoryId()));
            }

            // Filter by minimum price
            if (criteria.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("price"),
                        criteria.getMinPrice()
                ));
            }

            // Filter by maximum price
            if (criteria.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("price"),
                        criteria.getMaxPrice()
                ));
            }

            // Filter by featured status
            if (criteria.getFeatured() != null) {
                predicates.add(criteriaBuilder.equal(root.get("featured"), criteria.getFeatured()));
            }

            // Filter by in-stock products
            if (criteria.getInStock() != null && criteria.getInStock()) {
                predicates.add(criteriaBuilder.greaterThan(root.get("stockQuantity"), 0));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
