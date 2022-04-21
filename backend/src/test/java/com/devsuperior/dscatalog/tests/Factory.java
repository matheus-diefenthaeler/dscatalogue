package com.devsuperior.dscatalog.tests;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;

import java.time.Instant;

public class Factory {

  public static Product createProduct() {
    var product =
        new Product(
            1L,
            "Phone",
            "Good Phone",
            800.0,
            "https://img.com/img.jpg",
            Instant.parse("2022-04-21"));

    product.getCategories().add(new Category(2L, "Electronics"));

    return product;
  }

  public static ProductDTO createProductDTO() {
    var product = createProduct();
    return new ProductDTO(product, product.getCategories());
  }
}
