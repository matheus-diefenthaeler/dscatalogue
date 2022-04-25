package com.devsuperior.dscatalog.tests;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;

import java.time.Instant;

public class Factory {

    public static Category createCategory(){
        return new Category(1L, "Electronics");
    }

    public static Product createProduct() {
    var product =
        new Product(
            1L,
            "Phone",
            "Good Phone",
            800.0,
            "https://img.com/img.jpg",
            Instant.parse("2022-04-21T03:00:00Z"));

    product.getCategories().add(createCategory());

    return product;
  }

  public static ProductDTO createProductDTO() {
    var product = createProduct();
    return new ProductDTO(product, product.getCategories());
  }
}
