package com.example.stockstudy

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/testJmeter")
class TestController {

    @GetMapping
    fun test() {
        println("test")
    }
}
