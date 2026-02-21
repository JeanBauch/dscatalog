package com.jeanbauch.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jeanbauch.demo.entities.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

}
