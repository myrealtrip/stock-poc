package com.example.stockstudy.service

import com.example.stockstudy.annotation.DynamicDistributeLock
import com.example.stockstudy.model.ServiceType
import com.example.stockstudy.repository.StockWithOptimisticRepository
import org.springframework.stereotype.Service

@Service
class DynamicDeductService(
    val stockWithOptimisticRepository: StockWithOptimisticRepository
) : StockDeductService {
    override fun supports(serviceType: ServiceType): Boolean {
        return ServiceType.DYNAMIC == serviceType
    }

    @DynamicDistributeLock("#id")
    override fun deduct(id: Long) {
        try {
            val stock = stockWithOptimisticRepository.findById(1).get()
            stock.deduct()
            stockWithOptimisticRepository.save(stock)
        } catch (e: Exception) {
            println("예외 발생: ${e.message}")
        }
    }
}
