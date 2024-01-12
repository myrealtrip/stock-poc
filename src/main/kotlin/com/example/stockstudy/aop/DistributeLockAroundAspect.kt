package com.example.stockstudy.aop

import com.example.stockstudy.annotation.DistributeLock
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component

@Aspect
@Component
class DistributeLockAroundAspect(
    private val redissonClient: RedissonClient,
    private val distributeLockTransaction: DistributeLockTransactionProxy
){
    companion object {
        private const val REDISSON_LOCK_PREFIX = "LOCK:"
    }
    @Around("@annotation(com.example.stockstudy.annotation.DistributeLock)")
    fun acquireDistributeLockAndCallMethod(joinPoint: ProceedingJoinPoint): Any? {
        val signature = joinPoint.signature as MethodSignature
        val distributeLock = signature.method.getAnnotation(DistributeLock::class.java)

        val rLock = redissonClient.getLock(distributeLock.key)
        return try {
            if (!rLock.tryLock(distributeLock.waitTime, distributeLock.leaseTime, distributeLock.timeUnit)) {
                throw IllegalStateException("redis lock 획득에 실패했습니다")
            }
            distributeLockTransaction.proceed(joinPoint)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw InterruptedException()
        } finally {
            if(rLock.isHeldByCurrentThread) {
                rLock.unlock()
            }
        }
    }
}
