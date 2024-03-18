package com.lovisgod.testVisaTTP.domain.use_cases

import com.lovisgod.iswhpay.domain.HorizonRepository

import com.lovisgod.testVisaTTP.handlers.HorizonPayException
import com.lovisgod.testVisaTTP.models.datas.TerminalInfo

class SetTerminalConfigUseCase(private val repository: HorizonRepository) {
    @Throws(HorizonPayException::class)
    suspend operator fun invoke(terminalInfo: TerminalInfo): Int{
        return repository.setTerminalConfig(terminalInfo)
    }
}