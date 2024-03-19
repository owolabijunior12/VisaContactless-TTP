package com.lovisgod.testVisaTTP.domain.use_cases

import com.lovisgod.iswhpay.domain.HorizonRepository
import com.lovisgod.testVisaTTP.handlers.HorizonPayException

class SetPinKeyUseCase(val repository: HorizonRepository) {

    @Throws(HorizonPayException::class)
    suspend operator fun invoke(isDukpt: Boolean = true, key: String, ksn: String): Int{
        return repository.setPinKey(isDukpt, key, ksn)
    }
}