package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

  @InjectMocks private ProductService service;

  @Mock private ProductRepository repository;

  @Mock private CategoryRepository categoryRepository;

  private long existingId;
  private long nonExistingId;
  private long dependentId;
  private PageImpl<Product> page;
  private Product product;
  private ProductDTO productDTO;
  private Category category;

  @BeforeEach
  void setup() throws Exception {
    existingId = 1L;
    nonExistingId = 1000L;
    dependentId = 4L;
    product = Factory.createProduct();
    page = new PageImpl<>(List.of(product));
    productDTO = Factory.createProductDTO();
    category = Factory.createCategory();

    // ********** Delete Mock **********
    Mockito.doNothing().when(repository).deleteById(existingId);
    Mockito.doThrow(EmptyResultDataAccessException.class)
        .when(repository)
        .deleteById(nonExistingId);
    Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

    // ********** FindAll Mock **********
    Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

    // ********** FindById Mock **********
    Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
    Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

    // ********** Product getById Mock **********
    Mockito.when(repository.getById(existingId)).thenReturn(product);
    Mockito.when(repository.getById(nonExistingId)).thenThrow(EntityNotFoundException.class);

    // ********** Category getById Mock **********
    Mockito.when(categoryRepository.getById(existingId)).thenReturn(category);
    Mockito.when(categoryRepository.getById(nonExistingId)).thenThrow(EntityNotFoundException.class)
        .thenThrow(EntityNotFoundException.class);

    // ********** Save Mock **********
    Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
  }

  @Test
  public void UpdateShouldReturnProductDTOWhenIdExists() {
    var result = service.update(existingId, productDTO);

    Assertions.assertNotNull(result);
  }

  @Test
  public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
    Assertions.assertThrows(
        ResourceNotFoundException.class,
        () -> {
          service.update(nonExistingId, productDTO);
        });
  }

  @Test
  public void findByIdShouldReturnProductDTOWhenIdExists() {
    var result = service.findById(existingId);

    Assertions.assertNotNull(result);
    Mockito.verify(repository).findById(result.getId());
  }

  @Test
  public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
    Assertions.assertThrows(
        ResourceNotFoundException.class,
        () -> {
          service.findById(nonExistingId);
        });

    Mockito.verify(repository).findById(nonExistingId);
  }

  @Test
  public void findAllPagedShouldReturnPage() {

    var pageable = PageRequest.of(0, 10);

    Page<ProductDTO> result = service.findAllPaged(pageable);

    Assertions.assertNotNull(result);
    Mockito.verify(repository).findAll(pageable);
  }

  @Test
  public void deleteShouldDoNothingWhenIdExists() {

    Assertions.assertDoesNotThrow(
        () -> {
          service.delete(existingId);
        });

    Mockito.verify(repository).deleteById(existingId);
  }

  @Test
  public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesntExists() {

    Assertions.assertThrows(
        ResourceNotFoundException.class,
        () -> {
          service.delete(nonExistingId);
        });

    Mockito.verify(repository).deleteById(nonExistingId);
  }

  @Test
  public void deleteShouldThrowDataIntegrityViolationExceptionWhenIdDoesntExists() {

    Assertions.assertThrows(
        DataBaseException.class,
        () -> {
          service.delete(dependentId);
        });

    Mockito.verify(repository).deleteById(dependentId);
  }
}
