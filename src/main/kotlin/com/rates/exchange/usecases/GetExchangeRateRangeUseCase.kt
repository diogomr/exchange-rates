package com.rates.exchange.usecases

import com.rates.exchange.domain.ExchangeRate
import com.rates.exchange.exceptions.InvalidDateRangeException
import com.rates.exchange.exceptions.UnsupportedBaseCurrencyException
import com.rates.exchange.persistence.UsdExchangeRateReader
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.Currency

@Component
class GetExchangeRateRangeUseCase(
    private val usdExchangeRateReader: UsdExchangeRateReader,
) {

    fun get(base: Currency, currency: Currency, from: LocalDate, to: LocalDate): List<ExchangeRate> {

        if (base.currencyCode != "USD") {
            throw UnsupportedBaseCurrencyException("Base currency ${base.currencyCode} is not yet supported")
        }

        if (from.isAfter(to)) throw InvalidDateRangeException()

        return usdExchangeRateReader.getRange(currency, from, to)
    }
}
