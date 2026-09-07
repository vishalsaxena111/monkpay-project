package com.javaMonk.monkpay.merchant_service.dto.request;


import com.javaMonk.monkpay.common_lib.enums.Environment;

public record CreateApiKeyRequest(
        Environment environment
) {
}
