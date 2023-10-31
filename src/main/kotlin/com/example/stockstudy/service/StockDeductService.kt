package com.example.stockstudy.service

import com.example.stockstudy.model.ServiceType

interface StockDeductService {
    fun supports(serviceType: ServiceType): Boolean
    fun deduct(id: Long)
}
