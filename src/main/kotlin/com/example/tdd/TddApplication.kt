package com.example.tdd

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
class TddApplication

fun main(args: Array<String>) {
    runApplication<TddApplication>(*args)
}
