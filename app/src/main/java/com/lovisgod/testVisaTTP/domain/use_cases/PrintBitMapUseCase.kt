package com.lovisgod.testVisaTTP.domain.use_cases

import android.graphics.Bitmap
import com.lovisgod.iswhpay.domain.HorizonRepository
import com.lovisgod.iswhpay.ui.uiState.PrintingState

class PrintBitMapUseCase(private val repository: HorizonRepository) {

    suspend operator fun invoke(bitmap: Bitmap, printingState: PrintingState){
        return repository.printBitMap(bitmap, printingState)
    }
}
