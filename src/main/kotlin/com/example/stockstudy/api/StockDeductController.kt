package com.example.stockstudy.api

import com.example.stockstudy.api.dto.ServiceTypeRequest
import com.example.stockstudy.service.StockDeductService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

@RestController
@RequestMapping("/deduct")
class StockDeductController(
    val stockDeductServices: List<StockDeductService>,
) {
    @PostMapping
    fun testUseStockPerformance(@RequestBody request: ServiceTypeRequest): Long {
        val service = stockDeductServices.find { it.supports(request.type) }!!

        val executor = Executors.newFixedThreadPool(10)
        val tasks = mutableListOf<Callable<Unit>>()
        for (i: Int in 1..1000) {
            tasks.add(Callable {
                service.deduct(1)
            })
        }
        return measureTimeMillis {
            executor.invokeAll(tasks)
            executor.shutdown()
        }
    }

    @PostMapping("/{id}")
    fun deduct(
        @RequestBody request: ServiceTypeRequest,
        @PathVariable id: Long
    ) {
        val service = stockDeductServices.find { it.supports(request.type) }!!

        service.deduct(id)
    }
}
