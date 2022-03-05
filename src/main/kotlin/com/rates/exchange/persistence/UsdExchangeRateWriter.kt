package com.rates.exchange.persistence

import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.Currency
import java.util.TreeMap

@Component
class UsdExchangeRateWriter(
    private val usdExchangeRates: MutableMap<Currency, TreeMap<LocalDate, Double>>,
) {

    fun insert(currency: Currency, date: LocalDate, rate: Double) {
        if (usdExchangeRates[currency] == null) {
            usdExchangeRates[currency] = TreeMap<LocalDate, Double>()
        }
        usdExchangeRates[currency]!![date] = rate
    }
}
