package com.product.api.service;

import java.util.List;
import org.springframework.http.ResponseEntity;
import com.product.api.entity.ProductImage;
import com.product.api.dto.in.DtoProductImageIn;

public interface SvcProductImage {
	ResponseEntity<List<ProductImage>> getProductImages(Integer productId);
	ResponseEntity<String> createProductImage(Integer productId, DtoProductImageIn in);
	ResponseEntity<String> deleteProductImage(Integer productId, Integer productImageId);
}
