package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

  @InjectMocks private ProductService service;

  @Mock private ProductRepository repository;

  private long existingId;
  private long nonExistingId;

  @BeforeEach
  void setup() throws Exception {
    existingId = 1L;
    nonExistingId = 1000L;

    Mockito.doNothing().when(repository).deleteById(existingId);
    Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);

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

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> {
                    service.delete(nonExistingId);
                });

        Mockito.verify(repository).deleteById(nonExistingId);
    }
}
