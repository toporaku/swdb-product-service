package com.product.api.service;

import com.product.api.dto.in.DtoCategroryIn;
import com.product.api.entity.Category;

import java.util.List;

/**
 * Interfaz de servicio para operaciones de categoría.
 */
public interface SvcCategory {

    public List<Category> findAll();

    public List<Category> findActive();

    public void create(DtoCategroryIn in);

    public void update(DtoCategroryIn in, Integer id);

    public void enable(Integer id);

    public void disable(Integer id);
}
