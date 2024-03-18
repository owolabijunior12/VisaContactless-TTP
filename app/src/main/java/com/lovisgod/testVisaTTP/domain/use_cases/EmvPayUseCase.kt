package com.lovisgod.testVisaTTP.domain.use_cases

import android.content.Context
import com.lovisgod.iswhpay.domain.HorizonRepository
import com.lovisgod.testVisaTTP.handlers.HorizonPayException
import com.lovisgod.testVisaTTP.models.uiState.ReadCardStates

class EmvPayUseCase (private val repository: HorizonRepository) {

    @Throws(HorizonPayException::class)
    suspend operator fun invoke(amount:Long, readCardStates: ReadCardStates, context: Context){
        return repository.pay(amount, readCardStates, context)
    }
}