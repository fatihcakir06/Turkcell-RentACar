package com.turkcell.rentACarProject.api.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkcell.rentACarProject.business.abstracts.RentalService;
import com.turkcell.rentACarProject.business.dtos.rental.ListRentalDto;
import com.turkcell.rentACarProject.business.requests.rental.CreateRentalRequest;
import com.turkcell.rentACarProject.business.requests.rental.DeleteRentalRequest;
import com.turkcell.rentACarProject.business.requests.rental.UpdateRentalRequest;
import com.turkcell.rentACarProject.core.exceptions.BusinessException;
import com.turkcell.rentACarProject.core.utilities.results.DataResult;
import com.turkcell.rentACarProject.core.utilities.results.Result;

@RestController
@RequestMapping("/api/rentals")
public class RentalsController {
	
	private RentalService rentalService;
	
	@Autowired
	public RentalsController(RentalService rentalService) {
		this.rentalService = rentalService;
	}
	
	@PostMapping("/createCorporateCustomer")
	Result createCorporateCustomer(@RequestBody @Valid CreateRentalRequest createRentalRequest) throws BusinessException {
		return this.rentalService.createForCorporateCustomer(createRentalRequest);
	}
	
	@PostMapping("/createIndividualCustomer")
	Result createIndividualCustomer(@RequestBody @Valid CreateRentalRequest createRentalRequest) throws BusinessException {
		return this.rentalService.createForIndividualCustomer(createRentalRequest);
	}
	
	@DeleteMapping("/delete")
	Result delete(DeleteRentalRequest deleteCarRequest) {
		return this.rentalService.delete(deleteCarRequest);		
	}

	@PutMapping("/updateCorporateCustomer")
	Result updateCorporateCustomer(UpdateRentalRequest updateCarRequest) {
		return this.rentalService.updateForCorporateCustomer(updateCarRequest);
	}
	
	@PutMapping("/updateIndividualCustomer")
	Result updateIndividualCustomer(UpdateRentalRequest updateCarRequest) {
		return this.rentalService.updateForIndividualCustomer(updateCarRequest);
	}

	@GetMapping("/getAll")
	DataResult<List<ListRentalDto>> getAll() {
		return this.rentalService.getAll();
	}
	
	@GetMapping("/getAllByCustomerId")
	DataResult<List<ListRentalDto>> getAllByCustomerId(int customerId) {
		return this.rentalService.getAllByCustomerId(customerId);
	}

	@GetMapping("/getById")
	DataResult<ListRentalDto> getById(int id) {
		return this.rentalService.getById(id);
	}

	
	
}
