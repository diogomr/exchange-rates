package com.rates.exchange.usecases

import com.rates.exchange.domain.ExchangeRate
import com.rates.exchange.exceptions.CurrencyExchangeRateNotFoundException
import com.rates.exchange.exceptions.UnsupportedBaseCurrencyException
import com.rates.exchange.persistence.UsdExchangeRateReader
import org.springframework.stereotype.Component
import java.util.Currency

@Component
class GetLatestExchangeRateUseCase(
    private val usdExchangeRateReader: UsdExchangeRateReader,
) {

    fun get(base: Currency, currency: Currency): ExchangeRate {

        if (base.currencyCode != "USD") {
            throw UnsupportedBaseCurrencyException("Base currency ${base.currencyCode} is not yet supported")
        }

        return usdExchangeRateReader.getLatest(currency)
            ?: throw CurrencyExchangeRateNotFoundException(
                "No exchange rates found between ${base.currencyCode} and ${currency.currencyCode}"
            )
    }
}
