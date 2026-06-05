package com.product.api.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.product.api.dto.in.DtoProductImageIn;
import com.product.api.entity.ProductImage;
import com.product.api.service.SvcProductImage;

@Tag(name = "Product Image", description = "Gestión de imágenes asociadas a un producto")
@RestController
@RequestMapping("/product")
public class CtrlProductImg {

    @Autowired
    SvcProductImage svcProductImage;

    @GetMapping("/{id}/image")
    public ResponseEntity<List<ProductImage>> getProductImages(@PathVariable Integer id) {
        return svcProductImage.getProductImages(id);
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<String> createProductImage(@PathVariable Integer id,
            @Valid @RequestBody DtoProductImageIn in) {
        return svcProductImage.createProductImage(id, in);
    }

    @DeleteMapping("/{id}/image/{productImageId}")
    public ResponseEntity<String> deleteProductImage(@PathVariable Integer id,
            @PathVariable Integer productImageId) {
        return svcProductImage.deleteProductImage(id, productImageId);
    }
}
