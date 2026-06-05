package com.product.api.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DtoProductImageIn {

	@JsonProperty("product_id")
	@NotNull(message = "El ID del producto es obligatorio")
	private Integer productId;

	@JsonProperty("image")
	@NotBlank(message = "Image cannot be blank")
	private String image;

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
