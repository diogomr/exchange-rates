package com.rates.exchange.persistence

import com.rates.exchange.domain.ExchangeRate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate
import java.util.Collections
import java.util.Currency
import java.util.TreeMap

@Configuration
class PersistenceConfiguration {

    // We can make this an ENV var for a scenario where more currencies need to be supported
    private val currenciesToLoad = setOf(
        "CHF",
        "CNY",
        "JPY",
        "KRW",
        "NOK",
        "SEK",
        "THB",
        "TWD",
    )

    @Bean
    fun exchangeRates(): MutableMap<Currency, TreeMap<LocalDate, Double>> {

        val exchanges = mutableMapOf<Currency, TreeMap<LocalDate, Double>>()

        currenciesToLoad.forEach { cur ->
            val exchangeMap = TreeMap<LocalDate, Double>()
            val currency = Currency.getInstance(cur)
            readExchangeRates(cur).forEach {
                exchangeMap[it.date] = it.rate
            }
            exchanges[currency] = exchangeMap
        }

        return Collections.synchronizedMap(exchanges)
    }


    private fun readExchangeRates(currency: String): List<ExchangeRate> {
        return PersistenceConfiguration::class.java.classLoader.getResource("${currency}USD.csv")!!
            .readText()
            .split("\n")
            .drop(1)
            .filter { it.isNotBlank() }
            .mapNotNull {
                val (date, rate) = it.split(",")
                try {
                    val parsedDate = LocalDate.parse(date)
                    val parsedRate = rate.toDouble()
                    ExchangeRate(parsedDate, parsedRate)
                } catch (ex: Exception) {
                    // Invalid values get filtered out with mapNotNull
                    return@mapNotNull null
                }
            }
    }
}
