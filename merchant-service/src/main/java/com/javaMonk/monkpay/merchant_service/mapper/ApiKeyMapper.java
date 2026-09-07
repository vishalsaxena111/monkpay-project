package com.javaMonk.monkpay.merchant_service.mapper;

import com.javaMonk.monkpay.merchant_service.dto.response.ApiKeyCreateResponse;
import com.javaMonk.monkpay.merchant_service.dto.response.ApiKeyResponse;
import com.javaMonk.monkpay.merchant_service.entity.ApiKey;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ApiKeyMapper {

    ApiKeyCreateResponse toCreateResponse(ApiKey apiKey);

    List<ApiKeyResponse> toResponseList(List<ApiKey> apiKeyList);
}
