package com.javaMonk.monkpay.merchant_service.dto.response;


import com.javaMonk.monkpay.common_lib.enums.BusinessType;
import com.javaMonk.monkpay.common_lib.enums.MerchantStatus;

import java.util.UUID;

public record MerchantResponse(
        UUID id,
        String name,
        String email,
        String businessName,
        BusinessType businessType,
        MerchantStatus merchantStatus
) {
}
