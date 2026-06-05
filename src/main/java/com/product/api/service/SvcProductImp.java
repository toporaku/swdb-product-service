package com.product.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.product.api.dto.in.DtoProductIn;
import com.product.api.dto.out.DtoProductListOut;
import com.product.api.dto.out.DtoProductOut;
import com.product.api.entity.Product;
import com.product.api.repository.RepoProduct;
import com.product.common.mapper.MapperProduct;
import com.product.exception.ApiException;
import com.product.exception.DBAccessException;

@Service
public class SvcProductImp implements SvcProduct {

	@Autowired
	RepoProduct repo;

	@Autowired
	MapperProduct mapper;

	@Override
	public ResponseEntity<List<DtoProductListOut>> getProducts() {
		try {
			List<Product> products = repo.findAll();
			return new ResponseEntity<>(mapper.fromProductList(products), HttpStatus.OK);
		} catch (DataAccessException e) {
			throw new DBAccessException(e);
		}
	}

	@Override
	public ResponseEntity<DtoProductOut> getProduct(Integer id) {
		try {
			validateProductId(id);
			Product product = repo.findById(id).get();
			return new ResponseEntity<>(mapper.fromProduct(product), HttpStatus.OK);
		} catch (DataAccessException e) {
			throw new DBAccessException(e);
		}
	}

	@Override
	public ResponseEntity<String> createProduct(DtoProductIn in) {
		try {
			repo.create(
				in.getGtin(),
				in.getProduct(),
				in.getDescription(),
				in.getPrice(),
				in.getStock(),
				in.getCategory_id()
			);
			return new ResponseEntity<>("El producto ha sido registrado", HttpStatus.CREATED);
		} catch (DataAccessException e) {
			if (e.getLocalizedMessage().contains("ux_product_gtin"))
				throw new ApiException(HttpStatus.CONFLICT, "El gtin del producto ya está registrado");
			if (e.getLocalizedMessage().contains("ux_product_product"))
				throw new ApiException(HttpStatus.CONFLICT, "El nombre del producto ya está registrado");
			if (e.getLocalizedMessage().contains("fk_product_category"))
				throw new ApiException(HttpStatus.NOT_FOUND, "El id de categoría no existe");

			throw new DBAccessException(e);
		}
	}

	@Override
	public ResponseEntity<String> updateProduct(Integer id, DtoProductIn in) {
		try {
			validateProductId(id);
			Product current = repo.findById(id).get();
			repo.update(
				in.getGtin(),
				in.getProduct(),
				in.getDescription(),
				in.getPrice(),
				in.getStock(),
				in.getCategory_id(),
				current.getStatus(),
				id
			);
			return new ResponseEntity<>("El producto ha sido actualizado", HttpStatus.OK);
		} catch (DataAccessException e) {
			if (e.getLocalizedMessage().contains("ux_product_gtin"))
				throw new ApiException(HttpStatus.CONFLICT, "El gtin del producto ya está registrado");
			if (e.getLocalizedMessage().contains("ux_product_product"))
				throw new ApiException(HttpStatus.CONFLICT, "El nombre del producto ya está registrado");
			if (e.getLocalizedMessage().contains("fk_product_category"))
				throw new ApiException(HttpStatus.NOT_FOUND, "El id de categoría no existe");

			throw new DBAccessException(e);
		}
	}

	@Override
	public ResponseEntity<String> enableProduct(Integer id) {
		try {
			validateProductId(id);
			repo.enable(id);
			return new ResponseEntity<>("El producto ha sido activado", HttpStatus.OK);
		} catch (DataAccessException e) {
			throw new DBAccessException(e);
		}
	}

	@Override
	public ResponseEntity<String> disableProduct(Integer id) {
		try {
			validateProductId(id);
			repo.disable(id);
			return new ResponseEntity<>("El producto ha sido desactivado", HttpStatus.OK);
		} catch (DataAccessException e) {
			throw new DBAccessException(e);
		}
	}

	// @spec PROD-INT-001
	@Override
	public ResponseEntity<DtoProductOut> getProductByGtin(String gtin) {
		try {
			Product product = repo.findByGtin(gtin)
					.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "El producto no existe"));
			return new ResponseEntity<>(mapper.fromProduct(product), HttpStatus.OK);
		} catch (DataAccessException e) {
			throw new DBAccessException(e);
		}
	}

	// @spec PROD-INT-002, PROD-INT-003, PROD-INT-004
	@Override
	public ResponseEntity<String> decrementStock(String gtin, Integer quantity) {
		if (quantity == null || quantity <= 0) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a cero");
		}
		try {
			Product product = repo.findByGtin(gtin)
					.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "El producto no existe"));
			if (product.getStatus() == 0) {
				throw new ApiException(HttpStatus.CONFLICT, "El producto está desactivado");
			}
			if (product.getStock() < quantity) {
				throw new ApiException(HttpStatus.CONFLICT, "Stock insuficiente");
			}
			repo.decrementStock(gtin, quantity);
			return new ResponseEntity<>("El stock del producto ha sido actualizado", HttpStatus.OK);
		} catch (DataAccessException e) {
			throw new DBAccessException(e);
		}
	}

	// @spec PROD-INT-005
	@Override
	public ResponseEntity<String> incrementStock(String gtin, Integer quantity) {
		if (quantity == null || quantity <= 0) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a cero");
		}
		try {
			repo.findByGtin(gtin)
					.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "El producto no existe"));
			repo.incrementStock(gtin, quantity);
			return new ResponseEntity<>("El stock del producto ha sido actualizado", HttpStatus.OK);
		} catch (DataAccessException e) {
			throw new DBAccessException(e);
		}
	}

	private void validateProductId(Integer id) {
		try {
			if (repo.findById(id).isEmpty()) {
				throw new ApiException(HttpStatus.NOT_FOUND, "El id del producto no existe");
			}
		} catch (DataAccessException e) {
			throw new DBAccessException(e);
		}
	}

}
