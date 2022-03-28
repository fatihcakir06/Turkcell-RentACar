package com.turkcell.rentACarProject.business.abstracts;

import java.util.List;

import com.turkcell.rentACarProject.business.dtos.rental.ListRentalDto;
import com.turkcell.rentACarProject.business.requests.rental.CreateRentalRequest;
import com.turkcell.rentACarProject.business.requests.rental.DeleteRentalRequest;
import com.turkcell.rentACarProject.business.requests.rental.UpdateRentalRequest;
import com.turkcell.rentACarProject.core.exceptions.BusinessException;
import com.turkcell.rentACarProject.core.utilities.results.DataResult;
import com.turkcell.rentACarProject.core.utilities.results.Result;

public interface RentalService {
	
	Result createForCorporateCustomer(CreateRentalRequest createRentalRequest);  
	
	Result createForIndividualCustomer(CreateRentalRequest createRentalRequest);  	
	
	Result delete(DeleteRentalRequest deleteCarRequest);

	Result update(UpdateRentalRequest updateCarRequest);

	DataResult<List<ListRentalDto>> getAll();
	
	DataResult<List<ListRentalDto>> getAllByCustomerId(int customerId);

	DataResult<ListRentalDto> getById(int id);

	Result isCarRented(int carId) throws BusinessException;
	
}
