package com.product.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.product.api.dto.in.DtoProductImageIn;
import com.product.api.entity.ProductImage;
import com.product.api.repository.RepoProductImage;
import com.product.api.repository.RepoProduct;
import com.product.exception.ApiException;

@Service
public class SvcProductImageImp implements SvcProductImage {

	@Autowired
	RepoProductImage repo;

	@Autowired
	RepoProduct repoProduct;

	@Value("${app.upload.dir}")
	private String uploadDir;

	@Value("${app.upload.images}")
	private String uploadImages;

	@Override
	public ResponseEntity<List<ProductImage>> getProductImages(Integer productId) {
		if (repoProduct.findById(productId).isEmpty()) {
			throw new ApiException(HttpStatus.NOT_FOUND, "El id del producto no existe");
		}
		return new ResponseEntity<>(repo.findByProduct_id(productId), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> createProductImage(Integer productId, DtoProductImageIn in) {
		if (repoProduct.findById(productId).isEmpty()) {
			throw new ApiException(HttpStatus.NOT_FOUND, "El id del producto no existe");
		}
		
		String base64Image = in.getImage();
		if (base64Image != null && base64Image.contains(",")) {
			// Remover el prefijo data:image/png;base64, si existe
			base64Image = base64Image.split(",")[1];
		}
		
		byte[] decodedBytes;
		try {
			decodedBytes = java.util.Base64.getDecoder().decode(base64Image);
		} catch (IllegalArgumentException e) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Base64 de imagen no válido");
		}

		String filename = java.util.UUID.randomUUID().toString() + ".png";
		try {
			// Usando application properties u "img" por defecto
			java.nio.file.Path directory = java.nio.file.Paths.get(uploadDir, "img", "product");
			if (!java.nio.file.Files.exists(directory)) {
				java.nio.file.Files.createDirectories(directory);
			}
			java.nio.file.Path filePath = directory.resolve(filename);
			java.nio.file.Files.write(filePath, decodedBytes);
		} catch (java.io.IOException e) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar la imagen en disco: " + e.getMessage());
		}

		ProductImage pi = new ProductImage();
		pi.setProduct_id(productId);
		pi.setImage(filename);
		pi.setStatus(1);
		repo.save(pi);
		return new ResponseEntity<>("La imagen ha sido registrada", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> deleteProductImage(Integer productId, Integer productImageId) {
		if (repoProduct.findById(productId).isEmpty()) {
			throw new ApiException(HttpStatus.NOT_FOUND, "El id del producto no existe");
		}
		ProductImage img = repo.findById(productImageId)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "La imagen no existe"));
		
		if (!img.getProduct_id().equals(productId)) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "La imagen no pertenece al producto indicado");
		}
		
		repo.delete(img);
		return new ResponseEntity<>("La imagen ha sido eliminada", HttpStatus.OK);
	}
}
