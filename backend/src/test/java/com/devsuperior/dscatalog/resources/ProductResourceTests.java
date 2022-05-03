package com.devsuperior.dscatalog.resources;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

  @Autowired private MockMvc mockMvc;

  @MockBean private ProductService service;

  @Autowired private ObjectMapper objectMapper;

  private Long existingId;
  private Long nonexistingId;
  private Long dependentId;
  private PageImpl<ProductDTO> page;
  private ProductDTO productDTO;

  @BeforeEach
  void setup() throws Exception {
    existingId = 1L;
    nonexistingId = 2L;
    dependentId = 3L;

    productDTO = Factory.createProductDTO();
    page = new PageImpl<>(List.of(productDTO));

    // ********** FindAllPaged Mock **********
    Mockito.when(service.findAllPaged(ArgumentMatchers.any())).thenReturn(page);

    // ********** FindAll Mock **********
    Mockito.when(service.findById(existingId)).thenReturn(productDTO);
    Mockito.when(service.findById(nonexistingId)).thenThrow(ResourceNotFoundException.class);

    // ********** Update Mock **********
    Mockito.when(service.update(ArgumentMatchers.eq(existingId), ArgumentMatchers.any()))
        .thenReturn(productDTO);
    Mockito.when(service.update(ArgumentMatchers.eq(nonexistingId), ArgumentMatchers.any()))
        .thenThrow(ResourceNotFoundException.class);

    // ********** Delete Mock **********
    Mockito.doNothing().when(service).delete(existingId);
    Mockito.doThrow(ResourceNotFoundException.class).when(service).delete(nonexistingId);
    Mockito.doThrow(DataBaseException.class).when(service).delete(dependentId);
  }

  @Test
  public void deleteShouldDoNothingWhenIdExist() throws Exception {

    ResultActions result =
        mockMvc.perform(delete("/products/{id}", existingId).accept(MediaType.APPLICATION_JSON));

    result.andExpect(status().isNoContent());
  }

  @Test
  public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

    ResultActions result =
        mockMvc.perform(delete("/products/{id}", nonexistingId).accept(MediaType.APPLICATION_JSON));

    result.andExpect(status().isNotFound());
  }

  @Test
  public void updateShouldReturnProductDTOWhenIdExists() throws Exception {

    String jsonBody = objectMapper.writeValueAsString(productDTO);

    ResultActions result =
        mockMvc.perform(
            put("/products/{id}", existingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

    result.andExpect(status().isOk());
    result.andExpect(jsonPath("$.id").exists());
    result.andExpect(jsonPath("$.name").exists());
    result.andExpect(jsonPath("$.description").exists());
  }

  @Test
  public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {

    String jsonBody = objectMapper.writeValueAsString(productDTO);

    ResultActions result =
        mockMvc.perform(
            put("/products/{id}", nonexistingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

    result.andExpect(status().isNotFound());
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
