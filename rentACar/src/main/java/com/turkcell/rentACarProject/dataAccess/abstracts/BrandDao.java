package com.turkcell.rentACarProject.dataAccess.abstracts;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.turkcell.rentACarProject.entities.concretes.Brand;

@Repository
public interface BrandDao extends JpaRepository<Brand, Integer> {

	Brand getBrandById(int id);

	boolean existsByName(String name);

	List<Brand> getBrandByName(String name);
}
