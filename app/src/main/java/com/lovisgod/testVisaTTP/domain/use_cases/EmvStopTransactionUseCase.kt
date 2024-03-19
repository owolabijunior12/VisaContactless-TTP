package com.lovisgod.testVisaTTP.domain.use_cases

import com.lovisgod.iswhpay.domain.HorizonRepository
import com.lovisgod.testVisaTTP.handlers.HorizonPayException

class EmvStopTransactionUseCase (private val repository: HorizonRepository) {

    @Throws(HorizonPayException::class)
    suspend operator fun invoke(){
        return repository.stopTransaction()
    }
}