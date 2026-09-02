package com.javaMonk.razorpay.payment_service.service;

import com.javaMonk.razorpay.payment_service.dto.request.PaymentInitRequest;
import com.javaMonk.razorpay.payment_service.dto.response.PaymentResponse;

import java.util.UUID;

public interface PaymentService {

    PaymentResponse initiate(UUID merchantId, PaymentInitRequest request, String idempotencyKey);

    PaymentResponse capture(UUID merchantId, UUID paymentId);

    void resolveAuthorization(UUID paymentId, boolean approve, String bankRef, String errorCode, String errorDescription);
}
