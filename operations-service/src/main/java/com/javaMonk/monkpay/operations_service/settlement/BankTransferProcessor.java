package com.javaMonk.monkpay.operations_service.settlement;

import com.javaMonk.monkpay.common_lib.entity.Money;
import com.javaMonk.monkpay.operations_service.settlement.dto.BankTransferResult;

import java.util.UUID;

public interface BankTransferProcessor {

    BankTransferResult initiate(UUID settlementId, UUID merchantId, Money amount,
                                String bankAccount, String ifsc);
}
