package com.turkcell.rentACarProject.business.concretes;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.turkcell.rentACarProject.business.abstracts.AdditionalServiceItemService;
import com.turkcell.rentACarProject.business.abstracts.CarService;
import com.turkcell.rentACarProject.business.abstracts.OrderedAdditionalServiceService;
import com.turkcell.rentACarProject.business.abstracts.PaymentService;
import com.turkcell.rentACarProject.business.abstracts.RentalService;
import com.turkcell.rentACarProject.business.constants.Messages;
import com.turkcell.rentACarProject.business.dtos.city.ListCityDto;
import com.turkcell.rentACarProject.business.dtos.orderedAdditionalService.ListOrderedAdditionalServiceDto;
import com.turkcell.rentACarProject.business.dtos.payment.ListPaymentDto;
import com.turkcell.rentACarProject.business.dtos.rental.ListRentalDto;
import com.turkcell.rentACarProject.business.requests.payment.CreatePaymentRequest;
import com.turkcell.rentACarProject.business.requests.payment.DeletePaymentRequest;
import com.turkcell.rentACarProject.core.adapters.abstracts.BankAdapterService;
import com.turkcell.rentACarProject.core.exceptions.BusinessException;
import com.turkcell.rentACarProject.core.utilities.mapping.ModelMapperService;
import com.turkcell.rentACarProject.core.utilities.results.DataResult;
import com.turkcell.rentACarProject.core.utilities.results.Result;
import com.turkcell.rentACarProject.core.utilities.results.SuccessDataResult;
import com.turkcell.rentACarProject.core.utilities.results.SuccessResult;
import com.turkcell.rentACarProject.dataAccess.abstracts.PaymentDao;
import com.turkcell.rentACarProject.entities.concretes.Payment;

@Service

public class PaymentManager implements PaymentService {

	private ModelMapperService modelMapperService;
	private PaymentDao paymentDao;
	private RentalService rentalService;
	private BankAdapterService bankAdapterService;
	private CarService carService;
	private AdditionalServiceItemService additionalServiceItemService;
	private OrderedAdditionalServiceService orderedAdditionalServiceService;

	@Autowired
	public PaymentManager(ModelMapperService modelMapperService, PaymentDao paymentDao, RentalService rentalService,
			BankAdapterService bankAdapterService, CarService carService,
			AdditionalServiceItemService additionalServiceItemService,
			OrderedAdditionalServiceService orderedAdditionalServiceService) {
		this.modelMapperService = modelMapperService;
		this.paymentDao = paymentDao;
		this.rentalService = rentalService;
		this.bankAdapterService = bankAdapterService;
		this.orderedAdditionalServiceService = orderedAdditionalServiceService;
		this.carService = carService;
		this.additionalServiceItemService = additionalServiceItemService;

	}

	@Override
	public Result addForCorporateCustomer(CreatePaymentRequest createPaymentRequest) {

		// this.rentalService.checkRentCarExists(createPaymentRequest.getRentalId());
		checkPaymentRentalId(createPaymentRequest.getRentalId());

		ListRentalDto rental = rentalService.getById(createPaymentRequest.getRentalId()).getData();

		double totalPrice = rentalCalculation(rental);

		this.bankAdapterService.checkIfLimitIsEnough(createPaymentRequest.getCardNo(), createPaymentRequest.getYear(),
				createPaymentRequest.getMounth(), createPaymentRequest.getCVV(), totalPrice);

		Payment payment = this.modelMapperService.forRequest().map(createPaymentRequest, Payment.class);

		payment.setTotalPayment(totalPrice);

		payment.setId(0);
		this.paymentDao.save(payment);

		return new SuccessResult();
	}
	
	@Override
	public Result addForIndividualCustomer(CreatePaymentRequest createPaymentRequest) {

		// this.rentalService.checkRentCarExists(createPaymentRequest.getRentalId());
		checkPaymentRentalId(createPaymentRequest.getRentalId());

		ListRentalDto rental = rentalService.getById(createPaymentRequest.getRentalId()).getData();

		double totalPrice = rentalCalculation(rental);

		this.bankAdapterService.checkIfLimitIsEnough(createPaymentRequest.getCardNo(), createPaymentRequest.getYear(),
				createPaymentRequest.getMounth(), createPaymentRequest.getCVV(), totalPrice);

		Payment payment = this.modelMapperService.forRequest().map(createPaymentRequest, Payment.class);

		payment.setTotalPayment(totalPrice);

		payment.setId(0);
		this.paymentDao.save(payment);

		return new SuccessResult();
	}

	@Override
	public Result delete(DeletePaymentRequest deletePaymentRequest) {

		checkPaymentExists(deletePaymentRequest.getPaymentId());

		Payment payment = this.modelMapperService.forRequest().map(deletePaymentRequest, Payment.class);
		this.paymentDao.deleteById(payment.getId());

		return new SuccessResult();
	}

	@Override
	public DataResult<List<ListPaymentDto>> getAll() {

		List<Payment> result = this.paymentDao.findAll();
		List<ListPaymentDto> response = result.stream()
				.map(payment -> this.modelMapperService.forDto().map(payment, ListPaymentDto.class))
				.collect(Collectors.toList());

		return new SuccessDataResult<List<ListPaymentDto>>(response);
	}

	@Override
	public DataResult<List<ListPaymentDto>> getAllPaged(int pageNo, int pageSize) {

		Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

		List<Payment> result = this.paymentDao.findAll(pageable).getContent();
		List<ListPaymentDto> response = result.stream()
				.map(payment -> this.modelMapperService.forDto().map(payment, ListPaymentDto.class))
				.collect(Collectors.toList());

		return new SuccessDataResult<List<ListPaymentDto>>(response);
	}

	@Override
	public DataResult<ListPaymentDto> getByRentalId(int rentalId) {

		// this.rentalService.checkRentCarExists(rentalId);

		List<Payment> result = this.paymentDao.getAllByRentalId(rentalId);
		ListPaymentDto response = this.modelMapperService.forDto().map(result, ListPaymentDto.class);

		return new SuccessDataResult<ListPaymentDto>(response);
	}

	@Override
	public boolean checkPaymentRentalId(int rentalId) {

		List<Payment> result = this.paymentDao.getAllByRentalId(rentalId);
		if (result != null) {
			throw new BusinessException(Messages.ThisPaymentAlreadyExists);
		}
		return true;
	}

	private boolean checkPaymentExists(int paymentId) {

		boolean result = this.paymentDao.existsById(paymentId);
		if (result) {
			return true;
		}
		throw new BusinessException(Messages.PaymentIdNotFound);
	}

	private double rentalCalculation(ListRentalDto rental) {

		double totalPrice = 0;

		List<ListOrderedAdditionalServiceDto> orderedAdditionalServiceDtos = orderedAdditionalServiceService
				.findAllByRentalId(rental.getId()).getData();

		if (orderedAdditionalServiceDtos.size() > 0) {
			for (ListOrderedAdditionalServiceDto orderedAdditionalServiceDto : orderedAdditionalServiceDtos) {
				totalPrice += additionalServiceItemService
						.findById(orderedAdditionalServiceDto.getAdditionalServiceItemId()).getData().getPrice();
			}
		}

		if (rental.getInitialCityId() != rental.getReturnCityId())
			totalPrice += 750;

		long days = ChronoUnit.DAYS.between(rental.getRentDate(), rental.getReturnDate());

		if (days == 0)
			days = 1;

		totalPrice += days * carService.getById(rental.getCarId()).getData().getCarDailyPrice();

		return totalPrice;
	}
}
