package com.product.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.product.api.entity.Product;

import jakarta.transaction.Transactional;

@Repository
public interface RepoProduct extends JpaRepository<Product, Integer> {

    @Query(value = "SELECT * FROM product ORDER BY product", nativeQuery = true)
    List<Product> findAll();

    @Query(value = "SELECT * FROM product WHERE status = 1 ORDER BY product", nativeQuery = true)
    List<Product> findActive();

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO product (gtin, product, description, price, stock, category_id, status) " +
                   "VALUES (:gtin, :product, :description, :price, :stock, :category_id, 1)", nativeQuery = true)
    void create(@Param("gtin") String gtin,
                @Param("product") String product,
                @Param("description") String description,
                @Param("price") Float price,
                @Param("stock") Integer stock,
                @Param("category_id") Integer category_id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE product SET gtin = :gtin, product = :product, description = :description, " +
                   "price = :price, stock = :stock, category_id = :category_id, status = :status " +
                   "WHERE product_id = :id", nativeQuery = true)
    void update(@Param("gtin") String gtin,
                @Param("product") String product,
                @Param("description") String description,
                @Param("price") Float price,
                @Param("stock") Integer stock,
                @Param("category_id") Integer category_id,
                @Param("status") Integer status,
                @Param("id") Integer id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE product SET status = 1 WHERE product_id = :id", nativeQuery = true)
    void enable(@Param("id") Integer id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE product SET status = 0 WHERE product_id = :id", nativeQuery = true)
    void disable(@Param("id") Integer id);

    @Query(value = "SELECT * FROM product WHERE gtin = :gtin", nativeQuery = true)
    java.util.Optional<Product> findByGtin(@Param("gtin") String gtin);

    @Modifying
    @Transactional
    @Query(value = "UPDATE product SET stock = stock - :quantity WHERE gtin = :gtin", nativeQuery = true)
    void decrementStock(@Param("gtin") String gtin, @Param("quantity") Integer quantity);

    @Modifying
    @Transactional
    @Query(value = "UPDATE product SET stock = stock + :quantity WHERE gtin = :gtin", nativeQuery = true)
    void incrementStock(@Param("gtin") String gtin, @Param("quantity") Integer quantity);
}
