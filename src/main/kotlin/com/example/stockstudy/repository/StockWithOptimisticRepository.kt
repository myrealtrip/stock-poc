package com.example.stockstudy.repository

import com.example.stockstudy.model.StockWithOptimisticLock
import org.springframework.data.jpa.repository.JpaRepository

interface StockWithOptimisticRepository : JpaRepository<StockWithOptimisticLock, Long> {

}
