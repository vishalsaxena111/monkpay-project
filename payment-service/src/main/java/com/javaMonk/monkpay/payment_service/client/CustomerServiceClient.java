package com.javaMonk.monkpay.payment_service.client;

import com.javaMonk.monkpay.common_lib.dto.FindOrCreateCustomerRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "merchant-service", path = "/internal/customers")
public interface CustomerServiceClient {

    @PostMapping("/find-or-create")
    UUID findOrCreate(@RequestBody FindOrCreateCustomerRequest request);

}
