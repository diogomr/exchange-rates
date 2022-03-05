package com.rates.exchange.domain

import java.time.LocalDate

data class ExchangeRate(
    val date: LocalDate,
    val rate: Double,
)
