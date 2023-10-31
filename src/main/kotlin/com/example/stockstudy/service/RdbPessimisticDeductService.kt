package com.example.stockstudy.service

import com.example.stockstudy.model.ServiceType
import com.example.stockstudy.model.Stock
import jakarta.persistence.EntityManager
import jakarta.persistence.LockModeType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RdbPessimisticDeductService(
    val entityManager: EntityManager
) : StockDeductService {

    @Transactional
    override fun deduct(id: Long) {
        val lockedStock = entityManager.find(Stock::class.java, id, LockModeType.PESSIMISTIC_WRITE)
        lockedStock.deduct()
    }

    override fun supports(serviceType: ServiceType): Boolean {
        return ServiceType.PESSIMISTIC == serviceType
    }
}
