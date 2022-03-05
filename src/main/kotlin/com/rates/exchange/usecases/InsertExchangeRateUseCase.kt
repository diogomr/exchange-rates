package com.rates.exchange.usecases

import com.rates.exchange.exceptions.CannotModifyExchangeRateException
import com.rates.exchange.exceptions.UnsupportedBaseCurrencyException
import com.rates.exchange.persistence.UsdExchangeRateReader
import com.rates.exchange.persistence.UsdExchangeRateWriter
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.Currency

@Component
class InsertExchangeRateUseCase(
    private val usdExchangeRateReader: UsdExchangeRateReader,
    private val usdExchangeRateWriter: UsdExchangeRateWriter,
) {

    fun insert(base: Currency, currency: Currency, date: LocalDate, rate: Double) {

        if (base.currencyCode != "USD") {
            throw UnsupportedBaseCurrencyException("Base currency ${base.currencyCode} is not yet supported")
        }

        val exchangeRate = usdExchangeRateReader.getRate(currency, date)
        if (exchangeRate != null) {
            if (exchangeRate.rate == rate) {
                // let's make this operation idempotent and not throw an error when no modification is done
                return
            } else {
                // trying to modify existing rates is probably a bad idea, especially past ones
                throw CannotModifyExchangeRateException()
            }
        }
        usdExchangeRateWriter.insert(currency, date, rate)
    }
}
