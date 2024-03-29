package com.example.stockstudy.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class DistributeLockTransactionProxy {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun proceed(
        joinPoint: ProceedingJoinPoint
    ): Any? {
        return joinPoint.proceed()
    }
}
