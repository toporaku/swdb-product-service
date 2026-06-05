package com.product.api.service;

import com.product.api.dto.in.DtoCategroryIn;
import com.product.api.entity.Category;
import com.product.api.repository.RepoCategory;
import com.product.exception.ApiException;
import com.product.exception.DBAccessException;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Implementación de servicio para operaciones de categoría utilizando JPA.
 */
@Service
public class SvcCategoryImp implements SvcCategory {

    @Autowired
    private RepoCategory repoCategory;

    @Override
    public List<Category> findAll() {
        try {
            return repoCategory.findAll();
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    @Override
    public List<Category> findActive() {
        try {
            return repoCategory.findActive();
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    @Override
    public void create(DtoCategroryIn in) {
        try {
            repoCategory.create(in.getCategory(), in.getTag());
        } catch (DataAccessException e) {
            if (e.getLocalizedMessage().contains("ux_category"))
                throw new ApiException(HttpStatus.CONFLICT, "La categoría ya existe");

            if (e.getLocalizedMessage().contains("ux_tag"))
                throw new ApiException(HttpStatus.CONFLICT, "La etiqueta ya existe");

            throw new DBAccessException(e);
        }
    }

    @Override
    public void update(DtoCategroryIn in, Integer id) {
        try {
            validateId(id);
            repoCategory.update(in.getCategory(), in.getTag(), id);
        } catch (DataAccessException e) {
            if (e.getLocalizedMessage().contains("ux_category"))
                throw new ApiException(HttpStatus.CONFLICT, "La categoría ya existe");

            if (e.getLocalizedMessage().contains("ux_tag"))
                throw new ApiException(HttpStatus.CONFLICT, "La etiqueta ya existe");

            throw new DBAccessException(e);
        }
    }

    @Override
    public void enable(Integer id) {
        try {
            validateId(id);
            repoCategory.enable(id);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    @Override
    public void disable(Integer id) {
        try {
            validateId(id);
            repoCategory.disable(id);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    private void validateId(Integer id) {
        if (repoCategory.findById(id).isEmpty())
            throw new ApiException(HttpStatus.NOT_FOUND,
                    "El id de la región no existe");
    }
}
