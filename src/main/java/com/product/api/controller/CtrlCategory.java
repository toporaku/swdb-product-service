package com.product.api.controller;

import com.product.api.dto.in.DtoCategroryIn;
import com.product.api.entity.Category;
import com.product.api.service.SvcCategory;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para el recurso de categoría.
 */
@Tag(name = "Category", description = "Catálogo de categorías de productos")
@RestController
@RequestMapping("/category")
public class CtrlCategory {

    @Autowired
    private SvcCategory svcCategory;

    @GetMapping
    public ResponseEntity<List<Category>> findAll() {
        return ResponseEntity.ok(svcCategory.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Category>> findActive() {
        return ResponseEntity.ok(svcCategory.findActive());
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody DtoCategroryIn in) {
        svcCategory.create(in);
        return ResponseEntity.ok().body("Categoría creada.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@RequestBody DtoCategroryIn in, @PathVariable Integer id) {
        svcCategory.update(in, id);
        return ResponseEntity.ok().body("Categoría actualizada.");
    }

    @PatchMapping("/{id}/enable")
    public ResponseEntity<String> enable(@PathVariable Integer id) {
        svcCategory.enable(id);
        return ResponseEntity.ok().body("Categoría activada.");
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<String> disable(@PathVariable Integer id) {
        svcCategory.disable(id);
        return ResponseEntity.ok("Categoría desactivada.");
    }
}
