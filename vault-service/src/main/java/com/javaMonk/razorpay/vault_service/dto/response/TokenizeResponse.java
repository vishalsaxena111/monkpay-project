package com.javaMonk.razorpay.vault_service.dto.response;


import com.javaMonk.razorpay.common_lib.enums.CardBrand;

public record TokenizeResponse(
        String token,
        String lastFour,
        CardBrand brand,
        Integer expiryMonth,
        Integer expiryYear
) {
}
