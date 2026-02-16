package com.jeanbauch.demo.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.jeanbauch.demo.dto.ProductDTO;
import com.jeanbauch.demo.entities.Category;
import com.jeanbauch.demo.entities.Product;
import com.jeanbauch.demo.repositories.CategoryRepository;
import com.jeanbauch.demo.repositories.ProductRepository;
import com.jeanbauch.demo.services.exceptions.DatabaseException;
import com.jeanbauch.demo.services.exceptions.ResourceNotFoundException;
import com.jeanbauch.demo.tests.Factory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private long existingIdCategory;
    private PageImpl<Product> page;
    private Product product;
    private Category category;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 3L;
        existingIdCategory = 2L;
        product = Factory.createProduct();
        category = Factory.createCategory();
        productDTO = Factory.createProductDTO();
        page = new PageImpl<>(List.of(product));
    }

    @Test
    public void findAllPagedShouldReturnPage() {
        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> result = service.findAllPaged(pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    public void findByIdPagedShouldReturnProductDTOWhenIdExist() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));

        ProductDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), existingId);
        Mockito.verify(repository, Mockito.times(1)).findById(existingId);
    }

    @Test
    public void findByIdPagedShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(existingId);
        });

        Mockito.verify(repository, Mockito.times(1)).findById(existingId);
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExist() {
        Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
        Mockito.when(categoryRepository.getReferenceById(existingIdCategory)).thenReturn(category);
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        ProductDTO result = service.update(existingId, productDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), existingId);

        Mockito.verify(repository).getReferenceById(existingId);
        Mockito.verify(repository).save(product);
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Mockito.when(repository.getReferenceById(nonExistingId))
                .thenThrow(EntityNotFoundException.class);

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(nonExistingId, productDTO));

        Assertions.assertEquals("Id not found " + nonExistingId, exception.getMessage());

        Mockito.verify(repository).getReferenceById(nonExistingId);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.when(repository.existsById(existingId)).thenReturn(true);

        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

        Mockito.verify(repository).deleteById(existingId);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });

        Mockito.verify(repository, Mockito.never()).deleteById(nonExistingId);
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);
        Mockito.doThrow(DataIntegrityViolationException.class)
                .when(repository)
                .deleteById(dependentId);

        DatabaseException exception = Assertions.assertThrows(
                DatabaseException.class,
                () -> service.delete(dependentId));

        Assertions.assertEquals("Integrity violation", exception.getMessage());

        Mockito.verify(repository).deleteById(dependentId);
        Mockito.verify(repository).existsById(dependentId);
    }

}
