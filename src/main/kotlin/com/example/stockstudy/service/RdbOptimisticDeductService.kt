package com.example.stockstudy.service

import com.example.stockstudy.model.ServiceType
import com.example.stockstudy.model.StockWithOptimisticLock
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RdbOptimisticDeductService(
    val entityManager: EntityManager
) : StockDeductService {
    @Transactional
    override fun deduct(id: Long) {
        val maxRetries = 3
        var currentTry = 0
        var waitTime = 50L

        while (true) {
            try {
                val stock = entityManager.find(StockWithOptimisticLock::class.java, id)
                stock.deduct()
                entityManager.flush()
                break
            } catch (e: Exception) {
                currentTry++

                if (currentTry >= maxRetries) {
                    println("재고 감소 작업에 실패했습니다. 최대 재시도 횟수를 초과했습니다.")
                    throw RuntimeException("재고 감소 작업에 실패했습니다. 최대 재시도 횟수를 초과했습니다.")
                }

                Thread.sleep(waitTime)
                waitTime *= 10

                entityManager.refresh(entityManager.find(StockWithOptimisticLock::class.java, id))
            }
        }
    }

    override fun supports(serviceType: ServiceType): Boolean {
        return ServiceType.OPTIMISTIC == serviceType
    }
}
