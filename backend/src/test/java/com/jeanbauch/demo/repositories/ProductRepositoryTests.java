package com.jeanbauch.demo.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.jeanbauch.demo.entities.Product;
import com.jeanbauch.demo.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;

    private long existId;
    private long nonExistingId;
    private long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception {
        existId = 1L;
        nonExistingId = 100L;
        countTotalProducts = 25L;
    }

    @Test
    public void findByIdShouldReturnNonEmptyOptionalProductWhenIdExists() {
        Optional<Product> result = repository.findById(existId);

        Assertions.assertTrue(result.isPresent());
    }

    @Test
    public void findByIdShouldReturnEmptyOptionalProductWhenIdDoesNotExist() {
        Optional<Product> result = repository.findById(nonExistingId);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
        Product product = Factory.createProduct();

        product.setId(null);
        product = repository.save(product);

        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(countTotalProducts + 1, product.getId());
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        repository.deleteById(existId);
        Optional<Product> result = repository.findById(existId);

        Assertions.assertFalse(result.isPresent());
    }
}
