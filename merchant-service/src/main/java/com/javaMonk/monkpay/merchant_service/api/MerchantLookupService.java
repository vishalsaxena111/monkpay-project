package com.javaMonk.monkpay.merchant_service.api;

import com.javaMonk.monkpay.common_lib.dto.SettlementBankDetails;
import com.javaMonk.monkpay.common_lib.dto.WebhookTarget;

import java.util.List;
import java.util.UUID;

public interface MerchantLookupService {

    List<WebhookTarget> getActiveConfigsForEvent(UUID merchantId, String eventType);

    List<UUID> listActiveMerchantIds();

    SettlementBankDetails getSettlementBankDetails(UUID merchantId);

}
