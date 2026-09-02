package com.javaMonk.razorpay.merchant_service.dto.request;


import com.javaMonk.razorpay.common_lib.enums.Environment;

public record CreateApiKeyRequest(
        Environment environment
) {
}
