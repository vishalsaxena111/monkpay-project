package com.javaMonk.monkpay.merchant_service.service;


import com.javaMonk.monkpay.merchant_service.dto.request.LoginRequest;
import com.javaMonk.monkpay.merchant_service.dto.request.MerchantSignupRequest;
import com.javaMonk.monkpay.merchant_service.dto.response.LoginResponse;
import com.javaMonk.monkpay.merchant_service.dto.response.MerchantResponse;

public interface AuthService {
    MerchantResponse signup(MerchantSignupRequest request);

    LoginResponse login(LoginRequest request);
}
