package com.jeanbauch.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jeanbauch.demo.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
