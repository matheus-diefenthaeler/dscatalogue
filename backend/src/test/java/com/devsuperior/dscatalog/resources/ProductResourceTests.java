package com.devsuperior.dscatalog.resources;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

  @Autowired private MockMvc mockMvc;

  @MockBean private ProductService service;

  private PageImpl<ProductDTO> page;

  private ProductDTO productDTO;

  @BeforeEach
  void setup() throws Exception {
    productDTO = Factory.createProductDTO();
    page = new PageImpl<>(List.of(productDTO));

    Mockito.when(service.findAllPaged(ArgumentMatchers.any())).thenReturn(page);
  }

  @Test
  public void findAllShouldReturnPage() {

    try {
      mockMvc
          .perform(MockMvcRequestBuilders.get("/products"))
          .andExpect(MockMvcResultMatchers.status().isOk());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
