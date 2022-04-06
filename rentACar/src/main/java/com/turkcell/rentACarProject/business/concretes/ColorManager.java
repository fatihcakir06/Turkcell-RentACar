package com.turkcell.rentACarProject.business.concretes;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turkcell.rentACarProject.business.abstracts.ColorService;
import com.turkcell.rentACarProject.business.constants.Messages;
import com.turkcell.rentACarProject.business.dtos.city.ListCityDto;
import com.turkcell.rentACarProject.business.dtos.color.ListColorDto;
import com.turkcell.rentACarProject.business.requests.color.CreateColorRequest;
import com.turkcell.rentACarProject.business.requests.color.DeleteColorRequest;
import com.turkcell.rentACarProject.business.requests.color.UpdateColorRequest;
import com.turkcell.rentACarProject.core.exceptions.BusinessException;
import com.turkcell.rentACarProject.core.utilities.mapping.ModelMapperService;
import com.turkcell.rentACarProject.core.utilities.results.Result;
import com.turkcell.rentACarProject.core.utilities.results.SuccessResult;
import com.turkcell.rentACarProject.dataAccess.abstracts.ColorDao;
import com.turkcell.rentACarProject.entities.concretes.Color;

@Service
public class ColorManager implements ColorService {

	private ColorDao colorDao;
	private ModelMapperService modelMapperService;

	@Autowired
	public ColorManager(ColorDao colorDao, ModelMapperService modelMapperService) {
		this.colorDao = colorDao;
		this.modelMapperService = modelMapperService;
	}

	@Override
	public Result create(CreateColorRequest createColorRequest) {

		checkIfColorExists(createColorRequest.getName());

		Color color = this.modelMapperService.forRequest().map(createColorRequest, Color.class);

		this.colorDao.save(color);

		return new SuccessResult();
	}

	@Override
	public List<ListColorDto> getAll() {
		
		List<Color> result = this.colorDao.findAll();
		List<ListColorDto> response = result.stream()
				.map(color -> this.modelMapperService.forDto().map(color, ListColorDto.class))
				.collect(Collectors.toList());
		return response;
	}

	@Override
	public ListColorDto getById(int id) {
		
		Color result = this.colorDao.getColorById(id);
		ListColorDto response = this.modelMapperService.forDto().map(result, ListColorDto.class);
		return response;
	}

	@Override
	public Result delete(DeleteColorRequest deleteColorRequest) {
		
		Color color = this.modelMapperService.forRequest().map(deleteColorRequest, Color.class);
		this.colorDao.delete(color);

		return new SuccessResult();

	}

	@Override
	public Result update(UpdateColorRequest updateColorRequest) {
		
		Color color = this.modelMapperService.forRequest().map(updateColorRequest, Color.class);
		this.colorDao.save(color);

		return new SuccessResult();

	}

	@Override
	public Result checkIfColorIdIsExists(int colorId) {
		if (colorDao.existsById(colorId)) {
			return new SuccessResult();
		}
		 throw new BusinessException(Messages.ColorIdNotFound);
	}

	void checkIfColorExists(String name) {
		
		if (colorDao.existsByName(name)) {
			
			throw new BusinessException(Messages.ColorAlreadyExists);
		}
	}

}
