package com.javaMonk.monkpay.payment_service.processor;


import com.javaMonk.monkpay.common_lib.dto.PaymentProcessorRequest;
import com.javaMonk.monkpay.common_lib.dto.PaymentProcessorResponse;

public interface PaymentProcessor {

    PaymentProcessorResponse charge(PaymentProcessorRequest request);

}
