package com.turkcell.rentACarProject.business.concretes;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turkcell.rentACarProject.business.abstracts.BrandService;
import com.turkcell.rentACarProject.business.constants.Messages;
import com.turkcell.rentACarProject.business.dtos.brand.ListBrandDto;
import com.turkcell.rentACarProject.business.requests.brand.CreateBrandRequest;
import com.turkcell.rentACarProject.business.requests.brand.DeleteBrandRequest;
import com.turkcell.rentACarProject.business.requests.brand.UpdateBrandRequest;
import com.turkcell.rentACarProject.core.exceptions.BusinessException;
import com.turkcell.rentACarProject.core.utilities.mapping.ModelMapperService;
import com.turkcell.rentACarProject.core.utilities.results.DataResult;
import com.turkcell.rentACarProject.core.utilities.results.Result;
import com.turkcell.rentACarProject.core.utilities.results.SuccessResult;
import com.turkcell.rentACarProject.dataAccess.abstracts.BrandDao;
import com.turkcell.rentACarProject.entities.concretes.Brand;
import com.turkcell.rentACarProject.entities.concretes.Rental;

@Service
public class BrandManager implements BrandService {

	private BrandDao brandDao;
	private ModelMapperService modelMapperService;

	@Autowired
	public BrandManager(BrandDao brandDao, ModelMapperService modelMapperService) {
		this.brandDao = brandDao;
		this.modelMapperService = modelMapperService;
	}

	@Override
	public List<ListBrandDto> getAll() {
		List<Brand> result = this.brandDao.findAll();
		List<ListBrandDto> response = result.stream()
				.map(brand -> this.modelMapperService.forDto().map(brand, ListBrandDto.class))
				.collect(Collectors.toList());
		return response;
	}

	@Override
	public void create(CreateBrandRequest createBrandRequest) {
		
		checkIfBrandExistsByName(createBrandRequest.getName());
		
		Brand brand = this.modelMapperService.forRequest().map(createBrandRequest, Brand.class);

		this.brandDao.save(brand);
	}

	@Override
	public ListBrandDto getById(int id) {
		checkIfBrandIdIsExists(id);
		
		Brand result = this.brandDao.getBrandById(id);
		ListBrandDto response = this.modelMapperService.forDto().map(result, ListBrandDto.class);
		return response;

	}

	@Override
	public void delete(DeleteBrandRequest deleteBrandRequest) {
		Brand brand = this.modelMapperService.forRequest().map(deleteBrandRequest, Brand.class);
		this.brandDao.delete(brand);

	}

	@Override
	public void update(UpdateBrandRequest updateBrandRequest) {
		Brand brand = this.modelMapperService.forRequest().map(updateBrandRequest, Brand.class);
		this.brandDao.save(brand);

	}
	

	@Override
	public Result checkIfBrandIdIsExists(int brandId) {
		if (!brandDao.existsById(brandId)) {
			throw new BusinessException(Messages.BrandIdNotFound);
		}
		
		return new SuccessResult();
	}
	
	private Result checkIfBrandExistsByName(String name) {
		if (brandDao.existsByName(name)) {
			throw new BusinessException(Messages.BrandNameIsAlreadyExists);
		}
		
		return  new  SuccessResult();
	}


	
}
