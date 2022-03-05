package com.rates.exchange.rest

import com.rates.exchange.domain.ExchangeRate
import com.rates.exchange.usecases.GetAllExchangeRatesForDayUseCase
import com.rates.exchange.usecases.GetExchangeRateRangeUseCase
import com.rates.exchange.usecases.GetLatestExchangeRateUseCase
import com.rates.exchange.usecases.InsertExchangeRateUseCase
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.Currency

@RestController
class ExchangeRatesController(
    private val getLatestExchangeRateUseCase: GetLatestExchangeRateUseCase,
    private val getExchangeRateRangeUseCase: GetExchangeRateRangeUseCase,
    private val getAllExchangeRatesForDayUseCase: GetAllExchangeRatesForDayUseCase,
    private val insertExchangeRateUseCase: InsertExchangeRateUseCase,
) {

    @GetMapping("/exchange-rates/latest")
    fun getLatest(
        @RequestParam(defaultValue = "USD") base: Currency,
        @RequestParam(required = true) currency: Currency,
    ): ExchangeRate {
        return getLatestExchangeRateUseCase.get(
            base = base,
            currency = currency
        )
    }

    @GetMapping("/exchange-rates")
    fun getRange(
        @RequestParam(defaultValue = "USD") base: Currency,
        @RequestParam(required = true) currency: Currency,
        @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate,
        @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate,
    ): List<ExchangeRate> {
        return getExchangeRateRangeUseCase.get(
            base = base,
            currency = currency,
            from = from,
            to = to
        )
    }

    @GetMapping("/exchange-rates/{day}")
    fun getAllForDay(
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) day: LocalDate,
        @RequestParam(defaultValue = "USD") base: Currency,
    ): Map<Currency, ExchangeRate> {
        return getAllExchangeRatesForDayUseCase.get(base, day)
    }

    @PostMapping("/exchange-rates")
    fun save(
        @RequestBody body: InsertExchangeRateBody,
    ) {
        insertExchangeRateUseCase.insert(
            base = body.base,
            currency = body.currency,
            date = body.date,
            rate = body.rate,
        )
    }

    data class InsertExchangeRateBody(
        val base: Currency,
        val currency: Currency,
        val date: LocalDate,
        val rate: Double,
    )
}
