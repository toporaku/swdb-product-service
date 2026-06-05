package com.product.api.repository;

import com.product.api.entity.Category;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para entidades {@link Category}.
 */
@Repository
public interface RepoCategory extends JpaRepository<Category, Integer> {

    @Query(value = "SELECT * FROM category ORDER BY category", nativeQuery = true)
    List<Category> findAll();

    @Query(value = "SELECT * FROM category WHERE status = 1 ORDER BY category", nativeQuery = true)
    List<Category> findActive();

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO category (category, tag, status) VALUES (:category, :tag, 1)", nativeQuery = true)
    void create(@Param("category") String category, @Param("tag") String tag);

    @Modifying
    @Transactional
    @Query(value = "UPDATE category SET category = :category, tag = :tag, status = :status WHERE category_id = :id", nativeQuery = true)
    void update(@Param("category") String category, @Param("tag") String tag,
            @Param("id") Integer id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE category SET status = 1 WHERE category_id = :id", nativeQuery = true)
    void enable(@Param("id") Integer id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE category SET status = 0 WHERE category_id = :id", nativeQuery = true)
    void disable(Integer id);
}
