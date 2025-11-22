package com.tinylink.backend.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tinylink.backend.model.Link;

public interface LinkRepo extends JpaRepository<Link, String> {

	 Optional<Link> findByCode(String code);
}
