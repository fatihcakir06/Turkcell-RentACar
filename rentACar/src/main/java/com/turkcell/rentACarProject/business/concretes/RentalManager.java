package com.turkcell.rentACarProject.business.concretes;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.turkcell.rentACarProject.business.abstracts.AdditionalServiceItemService;
import com.turkcell.rentACarProject.business.abstracts.CarMaintenanceService;
import com.turkcell.rentACarProject.business.abstracts.CarService;
import com.turkcell.rentACarProject.business.abstracts.OrderedAdditionalServiceService;
import com.turkcell.rentACarProject.business.abstracts.RentalService;
import com.turkcell.rentACarProject.business.constants.Messages;
import com.turkcell.rentACarProject.business.dtos.orderedAdditionalService.ListOrderedAdditionalServiceDto;
import com.turkcell.rentACarProject.business.dtos.rental.ListRentalDto;
import com.turkcell.rentACarProject.business.requests.rental.CreateRentalRequest;
import com.turkcell.rentACarProject.business.requests.rental.DeleteRentalRequest;
import com.turkcell.rentACarProject.business.requests.rental.UpdateRentalRequest;
import com.turkcell.rentACarProject.core.exceptions.BusinessException;
import com.turkcell.rentACarProject.core.utilities.mapping.ModelMapperService;
import com.turkcell.rentACarProject.core.utilities.results.DataResult;
import com.turkcell.rentACarProject.core.utilities.results.ErrorDataResult;
import com.turkcell.rentACarProject.core.utilities.results.Result;
import com.turkcell.rentACarProject.core.utilities.results.SuccessDataResult;
import com.turkcell.rentACarProject.core.utilities.results.SuccessResult;
import com.turkcell.rentACarProject.dataAccess.abstracts.RentalDao;
import com.turkcell.rentACarProject.entities.concretes.Rental;

@Service
public class RentalManager implements RentalService {

	private RentalDao rentalDao;
	private CarService carService;
	private ModelMapperService modelMapperService;
	private AdditionalServiceItemService additionalServiceItemService;
	private OrderedAdditionalServiceService orderedAdditionalServiceService;
	private CarMaintenanceService carMaintenanceService;

	@Autowired
	public RentalManager(RentalDao rentalDao, ModelMapperService modelMapperService,
			@Lazy CarMaintenanceService carMaintenanceService, CarService carService,
			OrderedAdditionalServiceService orderedAdditionalServiceService,
			AdditionalServiceItemService additionalServiceItemService) {

		this.rentalDao = rentalDao;
		this.modelMapperService = modelMapperService;
		this.carMaintenanceService = carMaintenanceService;
		this.carService = carService;
		this.orderedAdditionalServiceService = orderedAdditionalServiceService;
		this.additionalServiceItemService = additionalServiceItemService;
	}

	@Override
	public Result createForCorporateCustomer(CreateRentalRequest createRentalRequest) throws BusinessException {

		carMaintenanceService.isCarInMaintenance(createRentalRequest.getCarId());
		isCarRented(createRentalRequest.getCarId());
		checkCarIdExists(createRentalRequest.getCarId());

		Rental rental = this.modelMapperService.forRequest().map(createRentalRequest, Rental.class);
		this.rentalDao.save(rental);

		return new SuccessResult(Messages.RentalAdded);
	}

	@Override
	public Result createForIndividualCustomer(CreateRentalRequest createRentalRequest) throws BusinessException {

		carMaintenanceService.isCarInMaintenance(createRentalRequest.getCarId());
		isCarRented(createRentalRequest.getCarId());
		checkCarIdExists(createRentalRequest.getCarId());

		Rental rental = this.modelMapperService.forRequest().map(createRentalRequest, Rental.class);
		this.rentalDao.save(rental);

		return new SuccessResult(Messages.RentalAdded);
	}


	@Override
	public Result update(UpdateRentalRequest updateRentalRequest) {

		carMaintenanceService.isCarInMaintenance(updateRentalRequest.getCarId());

		Rental rental = this.modelMapperService.forRequest().map(updateRentalRequest, Rental.class);
		rental.setTotalPrice(rentalCalculation(rental));
		updateReturnMileage(rental);
		this.rentalDao.save(rental);

		return new SuccessResult(Messages.CarUpdated);
	}
	

	@Override
	public Result delete(DeleteRentalRequest deleteRentalRequest) {
		
		checkIfExistsRentalId(deleteRentalRequest.getId());
		
		
		Rental rental = this.modelMapperService.forRequest().map(deleteRentalRequest, Rental.class);
		this.rentalDao.delete(rental);

		return new SuccessResult(Messages.RentalDeleted);
	}

	@Override
	public DataResult<List<ListRentalDto>> getAll() {
		
		
	
		List<Rental> result = this.rentalDao.findAll();
		List<ListRentalDto> response = result.stream()
				.map(rental -> this.modelMapperService.forDto().map(rental, ListRentalDto.class))
				.collect(Collectors.toList());

		return new SuccessDataResult<List<ListRentalDto>>(response);
	}

	@Override
	public DataResult<List<ListRentalDto>> getAllByCustomerId(int customerId) {
		
		List<Rental> result = this.rentalDao.findAllByCustomerId(customerId);
		List<ListRentalDto> response = result.stream()
				.map(rental -> this.modelMapperService.forDto().map(rental, ListRentalDto.class))
				.collect(Collectors.toList());

		return new SuccessDataResult<List<ListRentalDto>>(response);
	}

	@Override
	public DataResult<ListRentalDto> getById(int id) {
		
		Rental result = this.rentalDao.getById(id);
		checkIfExistsRentalId(id);
		
			ListRentalDto response = this.modelMapperService.forDto().map(result, ListRentalDto.class);
			
			return new SuccessDataResult<ListRentalDto>(response);
		
		
	
		
	}

	@Override
	public Result isCarRented(int id)  {

		if (this.rentalDao.findByCarIdAndReturnDateIsNull(id) != null) {
			throw new BusinessException(Messages.ThisCarIsRental);
		} else
			return new SuccessResult();
	}

	private void updateReturnMileage(Rental rental) {

		carService.getById(rental.getCar().getId()).getData().setMileage(rental.getReturnMileage());
	}

	private void checkCarIdExists(int carId) {

		if (!this.carService.getById(carId).isSuccess()) {
			throw new BusinessException(Messages.RentalNotFoundCar);
		}

	}

	private double rentalCalculation(Rental rental) {
	
		double totalPrice = 0;
		
		List<ListOrderedAdditionalServiceDto> orderedAdditionalServiceDtos = orderedAdditionalServiceService.findAllByRentalId(rental.getId()).getData();
		
		if(orderedAdditionalServiceDtos.size() > 0) {
			for(ListOrderedAdditionalServiceDto orderedAdditionalServiceDto : orderedAdditionalServiceDtos) {
				totalPrice += additionalServiceItemService.findById(orderedAdditionalServiceDto.getAdditionalServiceItemId()).getData().getPrice(); 
			}	
		}
		
		if(rental.getInitialCity().getId() != rental.getReturnCity().getId())
			totalPrice += 750;

		long days = ChronoUnit.DAYS.between(rental.getRentDate(), rental.getReturnDate());
		
		if(days == 0)
			days = 1;
		
		totalPrice += days * carService.getById(rental.getCar().getId()).getData().getCarDailyPrice();

		return totalPrice;
	}
	
	private Result checkIfExistsRentalId(int id) {
		
		if (rentalDao.existsById(id)) {
			return new SuccessResult();
		}
		else {
			throw new BusinessException(Messages.RentalIdNotFound);
		}
		
		
		
	}

}
