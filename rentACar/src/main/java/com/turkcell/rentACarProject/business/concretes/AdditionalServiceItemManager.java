package com.turkcell.rentACarProject.business.concretes;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turkcell.rentACarProject.business.abstracts.AdditionalServiceItemService;
import com.turkcell.rentACarProject.business.constants.Messages;
import com.turkcell.rentACarProject.business.dtos.additionalServiceItem.ListAdditionalServiceItemDto;
import com.turkcell.rentACarProject.business.requests.additionalServiceItem.CreateAdditionalServiceItemRequest;
import com.turkcell.rentACarProject.core.exceptions.BusinessException;
import com.turkcell.rentACarProject.core.utilities.mapping.ModelMapperService;
import com.turkcell.rentACarProject.core.utilities.results.DataResult;
import com.turkcell.rentACarProject.core.utilities.results.ErrorDataResult;
import com.turkcell.rentACarProject.core.utilities.results.Result;
import com.turkcell.rentACarProject.core.utilities.results.SuccessDataResult;
import com.turkcell.rentACarProject.core.utilities.results.SuccessResult;
import com.turkcell.rentACarProject.dataAccess.abstracts.AdditionalServiceItemDao;
import com.turkcell.rentACarProject.entities.concretes.AdditionalServiceItem;

@Service
public class AdditionalServiceItemManager implements AdditionalServiceItemService {

	private ModelMapperService modelMapperService;
	private AdditionalServiceItemDao additionalServiceItemDao;

	@Autowired
	public AdditionalServiceItemManager(ModelMapperService modelMapperService,
			AdditionalServiceItemDao additionalServiceItemDao) {

		this.modelMapperService = modelMapperService;
		this.additionalServiceItemDao = additionalServiceItemDao;
	}

	@Override
	public Result add(CreateAdditionalServiceItemRequest createAdditionalServiceItemRequest) {
		
		checkIfAdditionalServiceItemExistsByName(createAdditionalServiceItemRequest.getName());
		
		AdditionalServiceItem additionalServiceItem = this.modelMapperService.forRequest().map(createAdditionalServiceItemRequest, AdditionalServiceItem.class);
		this.additionalServiceItemDao.save(additionalServiceItem);
		return new SuccessResult();
	}

	@Override
	public DataResult<ListAdditionalServiceItemDto> findById(int id) {
		
		checkIfAdditionalServiceExistsById(id);
		
		AdditionalServiceItem item = additionalServiceItemDao.findById(id).get();
			ListAdditionalServiceItemDto response = modelMapperService.forDto().map(item, ListAdditionalServiceItemDto.class);
		
		
			
			return new SuccessDataResult<ListAdditionalServiceItemDto>(response);
		
	}

	@Override
	public DataResult<List<ListAdditionalServiceItemDto>> getAll() {
		
		var result = this.additionalServiceItemDao.findAll();
		
		List<ListAdditionalServiceItemDto> response = result.stream()
				.map(additionalServiceItem -> this.modelMapperService.forDto().map(additionalServiceItem, ListAdditionalServiceItemDto.class))
				.collect(Collectors.toList());
		
		return new SuccessDataResult<List<ListAdditionalServiceItemDto>>(response);
	}
	
	private Result checkIfAdditionalServiceItemExistsByName(String itemName) {
		
		if (additionalServiceItemDao.existsByColorName(itemName)) {
			throw new BusinessException(Messages.AdditionalServiceItemAlreadyExists);
		}
		
		return new SuccessResult();
	}
	
	private Result checkIfAdditionalServiceExistsById(int itemId) {
        if(!this.additionalServiceItemDao.existsById(itemId)) {
            throw new BusinessException(Messages.AdditionalServiceItemIsNotFound);
        }  
        return new SuccessResult();
        
    }
	
	

	
	
}
