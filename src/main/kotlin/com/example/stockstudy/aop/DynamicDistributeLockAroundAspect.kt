package com.example.stockstudy.aop

import com.example.stockstudy.annotation.DynamicDistributeLock
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component

@Aspect
@Component
class DynamicDistributeLockAroundAspect(
    private val redissonClient: RedissonClient,
    private val distributeLockTransaction: DistributeLockTransactionProxy
) {
    companion object {
        private const val REDISSON_LOCK_PREFIX = "EXPERIENCE:LOCK:"
    }

    @Around("@annotation(com.example.stockstudy.annotation.DynamicDistributeLock)")
    fun acquireDistributeLockAndCallMethod(joinPoint: ProceedingJoinPoint): Any? {
        val signature = joinPoint.signature as MethodSignature

        val lockMeta = getLockMeta(signature)
        val dynamicKey = REDISSON_LOCK_PREFIX + getDynamicKey(joinPoint, lockMeta.key)
        val rLock = redissonClient.getLock(dynamicKey)

        return tryLockAndProceed(joinPoint, rLock, lockMeta)
    }

    private fun tryLockAndProceed(joinPoint: ProceedingJoinPoint, rLock: RLock, lockMeta: DynamicDistributeLock): Any? {
        acquireLockOrThrow(rLock, lockMeta)
        return try {
            distributeLockTransaction.proceed(joinPoint)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw e
        } finally {
            releaseLockIfHeld(rLock)
        }
    }

    private fun getLockMeta(signature: MethodSignature): DynamicDistributeLock {
        return signature.method.getAnnotation(DynamicDistributeLock::class.java)
    }

    private fun acquireLockOrThrow(rLock: RLock, distributeLock: DynamicDistributeLock) {
        val lockAcquired = rLock.tryLock(distributeLock.waitTime, distributeLock.leaseTime, distributeLock.timeUnit)
        if (!lockAcquired) {
            throw Exception("[key : ${rLock.name}] redis lock 획득에 실패했습니다")
        }
    }

    private fun releaseLockIfHeld(rLock: RLock) {
        if (rLock.isHeldByCurrentThread) {
            rLock.unlock()
        }
    }

    private fun getDynamicKey(joinPoint: ProceedingJoinPoint, keyExpression: String): String {
        return CustomSpringELParser.getDynamicKey(
            (joinPoint.signature as MethodSignature).parameterNames,
            joinPoint.args,
            keyExpression
        ).toString()
    }
}
