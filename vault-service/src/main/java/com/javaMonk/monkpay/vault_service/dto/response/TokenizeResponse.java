package com.javaMonk.monkpay.vault_service.dto.response;


import com.javaMonk.monkpay.common_lib.enums.CardBrand;

public record TokenizeResponse(
        String token,
        String lastFour,
        CardBrand brand,
        Integer expiryMonth,
        Integer expiryYear
) {
}
