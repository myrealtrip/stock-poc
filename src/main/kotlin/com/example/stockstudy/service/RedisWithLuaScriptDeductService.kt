package com.example.stockstudy.service

import com.example.stockstudy.model.ServiceType
import org.redisson.api.RScript
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service

/*
 * 남은 재고량을 redis에서 관리한다고 가정합니다
 * redis가 메모리 기반의 디비이기 때문에, 정확한 이력을 rdb에 남기기 위해서는 추가 작업이 필요할 수 있습니다.
 */
@Service
class RedisWithLuaScriptDeductService(
    val redissonClient: RedissonClient
) : StockDeductService {
    private val stockKey = "stock:remain_quantity"
    private val luaScript = """
    local remainQuantityStr = redis.call('GET', KEYS[1])
    if remainQuantityStr == false then
        return "nil value"
    end
    local remainQuantity = tonumber(remainQuantityStr)
    if remainQuantity > 0 then
        redis.call('DECRBY', KEYS[1], ARGV[1])
        return tonumber(redis.call('GET', KEYS[1]))
    else
        return -1
    end
"""

    override fun supports(serviceType: ServiceType): Boolean {
        return ServiceType.LUA == serviceType
    }

    override fun deduct(id: Long) {
        val result = redissonClient.script.eval<Any>(
            RScript.Mode.READ_WRITE,
            luaScript,
            RScript.ReturnType.VALUE,
            listOf(stockKey),
            1
        )
        if (result == -1L) {
            println("재고가 부족합니다.")
        }
    }
}
