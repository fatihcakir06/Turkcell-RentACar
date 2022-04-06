package com.turkcell.rentACarProject.business.abstracts;

import java.util.List;

import com.turkcell.rentACarProject.business.dtos.color.ListColorDto;
import com.turkcell.rentACarProject.business.requests.color.CreateColorRequest;
import com.turkcell.rentACarProject.business.requests.color.DeleteColorRequest;
import com.turkcell.rentACarProject.business.requests.color.UpdateColorRequest;
import com.turkcell.rentACarProject.core.exceptions.BusinessException;
import com.turkcell.rentACarProject.core.utilities.results.Result;

public interface ColorService {

	List<ListColorDto> getAll();
	ListColorDto getById(int id) throws BusinessException;
	
	Result checkIfColorIdIsExists(int colorId);
	
	Result create(CreateColorRequest createColorRequest) ;
	Result delete(DeleteColorRequest deleteColorRequest);
	Result update(UpdateColorRequest updateColorRequest);
}
