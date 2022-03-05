package com.rates.exchange.persistence

import com.rates.exchange.domain.ExchangeRate
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.Currency
import java.util.TreeMap

@Component
class UsdExchangeRateReader(
    private val usdExchangeRates: Map<Currency, TreeMap<LocalDate, Double>>,
) {

    fun getLatest(currency: Currency): ExchangeRate? {
        return usdExchangeRates[currency]?.lastEntry()?.let { (k, v) ->
            ExchangeRate(k, v)
        }
    }

    fun getRange(currency: Currency, from: LocalDate, to: LocalDate): List<ExchangeRate> {
        val range = mutableListOf<ExchangeRate>()
        usdExchangeRates[currency]?.subMap(from, to)?.entries?.forEach { (k, v) ->
            range.add(ExchangeRate(k, v))
        }
        return range
    }

    fun getRate(currency: Currency, date: LocalDate): ExchangeRate? {
        return usdExchangeRates[currency]?.get(date)?.let {
            ExchangeRate(date, it)
        }
    }

    fun getExchangeCurrencies() = usdExchangeRates.keys
}
