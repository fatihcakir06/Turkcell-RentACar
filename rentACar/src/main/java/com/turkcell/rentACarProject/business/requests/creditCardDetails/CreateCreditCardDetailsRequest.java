package com.turkcell.rentACarProject.business.requests.creditCardDetails;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCreditCardDetailsRequest {
	
	
	private String cardNumber;
	
	
	private int cVV;
	private int year;
	private int month;
	
	private int customerId;
}
