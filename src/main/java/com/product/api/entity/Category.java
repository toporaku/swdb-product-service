package com.product.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Clase modelo de Categoría, ahora como Entidad JPA.
 * Representa una entidad de categoría con un id, nombre, etiqueta y estatus.
 */
@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("category_id")
    @Column(name = "category_id")
    private Integer categoryId;

    @JsonProperty("category")
    @Column(name = "category", nullable = false, unique = true)
    private String category;

    @JsonProperty("tag")
    @Column(name = "tag", nullable = false, unique = true)
    private String tag;

    @JsonProperty("status")
    @Column(name = "status", nullable = false)
    private Integer status;

    public Category() {
    }

    public Category(Integer categoryId, String category, String tag, Integer status) {
        this.categoryId = categoryId;
        this.category = category;
        this.tag = tag;
        this.status = status;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Category {"
                + "categoryId=" + categoryId
                + ", category='" + category + '\''
                + ", tag='" + tag + '\''
                + ", status=" + status
                + '}';
    }
}
