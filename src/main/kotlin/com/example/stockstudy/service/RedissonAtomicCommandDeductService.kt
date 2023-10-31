package com.example.stockstudy.service

import com.example.stockstudy.model.ServiceType
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service

@Service
class RedissonAtomicCommandDeductService(
    val redissonClient: RedissonClient
) : StockDeductService {
    private val stockKey = "stock:remain_quantity"

    override fun supports(serviceType: ServiceType): Boolean {
        return ServiceType.REDISSON_ATOMIC_COMMAND == serviceType
    }

    override fun deduct(id: Long) {
        val remainQuantity = redissonClient.getAtomicLong(stockKey)

        while (true) {
            val currentValue = remainQuantity.get()

            if (currentValue <= 0) {
                println("재고가 부족합니다.")
                return
            }

            if (remainQuantity.compareAndSet(currentValue, currentValue - 1)) {
                break
            }
        }
    }
}
