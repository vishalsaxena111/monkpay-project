package com.javaMonk.monkpay.merchant_service.dto.response;


import com.javaMonk.monkpay.common_lib.enums.Environment;

import java.util.UUID;

public record ApiKeyCreateResponse(
        UUID id,
        String keyId,
        String keySecret,
        Environment environment
) {
}
