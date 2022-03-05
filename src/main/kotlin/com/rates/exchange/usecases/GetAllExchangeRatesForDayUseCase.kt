package com.rates.exchange.usecases

import com.rates.exchange.domain.ExchangeRate
import com.rates.exchange.exceptions.UnsupportedBaseCurrencyException
import com.rates.exchange.persistence.UsdExchangeRateReader
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.Currency

@Component
class GetAllExchangeRatesForDayUseCase(
    private val usdExchangeRateReader: UsdExchangeRateReader,
) {

    fun get(base: Currency, day: LocalDate): Map<Currency, ExchangeRate> {

        if (base.currencyCode != "USD") {
            throw UnsupportedBaseCurrencyException("Base currency ${base.currencyCode} is not yet supported")
        }

        val result = mutableMapOf<Currency, ExchangeRate>()
        usdExchangeRateReader.getExchangeCurrencies().forEach { cur ->
            usdExchangeRateReader.getRate(cur, day)?.let {
                result[cur] = it
            }
        }
        return result
    }
}
