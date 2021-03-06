package com.turkcell.rentACarProject.core.utilities.business;

import com.turkcell.rentACarProject.core.utilities.results.Result;

public class BusinessRules {

    public static Result run(Result... logics) {
       
    	for (Result logic : logics) {
            if (!logic.isSuccess()) {
                return logic;
            }
        }
        return null;
    }
}