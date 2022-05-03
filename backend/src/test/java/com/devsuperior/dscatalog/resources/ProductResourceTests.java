package com.devsuperior.dscatalog.resources;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

  @Autowired private MockMvc mockMvc;

  @MockBean private ProductService service;

  private Long existingId;
  private Long nonexistingId;
  private PageImpl<ProductDTO> page;
  private ProductDTO productDTO;

  @BeforeEach
  void setup() throws Exception {
    existingId = 1L;
    nonexistingId = 2L;

    productDTO = Factory.createProductDTO();
    page = new PageImpl<>(List.of(productDTO));

    Mockito.when(service.findAllPaged(ArgumentMatchers.any())).thenReturn(page);
    Mockito.when(service.findById(existingId)).thenReturn(productDTO);
    Mockito.when(service.findById(nonexistingId)).thenThrow(ResourceNotFoundException.class);
  }

  @Test
  public void findAllShouldReturnPage() throws Exception {

    ResultActions result = mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON));

    result.andExpect(status().isOk());
  }

  @Test
  public void findByIdReturnProductWhenIdExists() throws Exception {

    ResultActions result =
        mockMvc.perform(get("/products/{id}", existingId).accept(MediaType.APPLICATION_JSON));

    result.andExpect(status().isOk());
    result.andExpect(jsonPath("$.id").exists());
    result.andExpect(jsonPath("$.name").exists());
    result.andExpect(jsonPath("$.description").exists());

  }

  @Test
  public void findByIdReturnResourceNotFoundWhenIdDoesNotExists() throws Exception {
    ResultActions result =
            mockMvc.perform(get("/products/{id}", nonexistingId).accept(MediaType.APPLICATION_JSON));

    result.andExpect(status().isNotFound());

  }
}
