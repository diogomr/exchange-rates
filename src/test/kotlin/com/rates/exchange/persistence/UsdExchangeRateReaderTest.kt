package com.rates.exchange.persistence

import com.rates.exchange.domain.ExchangeRate
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.hasSize
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo
import strikt.assertions.isNull
import java.time.LocalDate
import java.util.Currency
import java.util.TreeMap

internal class UsdExchangeRateReaderTest {

    @Test
    fun `getLatest always returns the latest date`() {
        // Given
        val chf = Currency.getInstance("CHF")
        val now = LocalDate.now()
        val rates = mapOf(
            chf to TreeMap(
                mapOf(
                    now to 1.1,
                    now.minusDays(1) to 1.2,
                    now.plusDays(1) to 2.0,
                    now.minusDays(2) to 1.2,
                )
            )
        )

        val subject = UsdExchangeRateReader(rates)

        // When
        val result = subject.getLatest(chf)

        // Then
        expectThat(result).isEqualTo(ExchangeRate(now.plusDays(1), 2.0))
    }

    @Test
    fun `getLatest returns null when currency is not in map`() {
        // Given
        val chf = Currency.getInstance("CHF")
        val rates = mapOf<Currency, TreeMap<LocalDate, Double>>()

        val subject = UsdExchangeRateReader(rates)

        // When
        val result = subject.getLatest(chf)

        // Then
        expectThat(result).isNull()
    }

    @Test
    fun `getRange returns empty list when requested dates are out of bounds`() {
        // Given
        val chf = Currency.getInstance("CHF")
        val now = LocalDate.now()
        val rates = mapOf(
            chf to TreeMap(
                mapOf(
                    now to 1.1,
                    now.minusDays(1) to 1.2,
                    now.plusDays(1) to 2.0,
                    now.minusDays(2) to 1.2,
                )
            )
        )

        val subject = UsdExchangeRateReader(rates)

        // When
        val result = subject.getRange(chf, now.minusDays(20), now.minusDays(10))

        // Then
        expectThat(result).isEmpty()
    }

    @Test
    fun `getRange returns correct range and is correctly ordered`() {
        // Given
        val chf = Currency.getInstance("CHF")
        val now = LocalDate.now()
        val rates = mapOf(
            chf to TreeMap(
                mapOf(
                    now to 1.1,
                    now.minusDays(1) to 1.2,
                    now.plusDays(1) to 2.0,
                    now.minusDays(2) to 1.2,
                )
            )
        )

        val subject = UsdExchangeRateReader(rates)

        // When
        val result = subject.getRange(chf, now, now.plusDays(300))

        // Then
        expectThat(result) {
            hasSize(2)
            containsExactly(ExchangeRate(now, 1.1), ExchangeRate(now.plusDays(1), 2.0))
        }
    }

    @Test
    fun `getRate returns correct exchange rate`() {
        // Given
        val chf = Currency.getInstance("CHF")
        val now = LocalDate.now()
        val rates = mapOf(
            chf to TreeMap(
                mapOf(
                    now to 1.0,
                    now.plusDays(1) to 2.0,
                )
            )
        )

        val subject = UsdExchangeRateReader(rates)

        // When
        val result = subject.getRate(chf, now)

        // Then
        expectThat(result).isEqualTo(ExchangeRate(now, 1.0))
    }

    @Test
    fun `getRate when missing date is requested then it returns null`() {
        // Given
        val chf = Currency.getInstance("CHF")
        val now = LocalDate.now()
        val rates = mapOf(
            chf to TreeMap(
                mapOf(
                    now to 1.0,
                    now.plusDays(1) to 2.0,
                )
            )
        )

        val subject = UsdExchangeRateReader(rates)

        // When
        val result = subject.getRate(chf, now.plusDays(30))

        // Then
        expectThat(result).isNull()
    }

    @Test
    fun `getRate when missing currency is requested then it returns null`() {
        // Given
        val now = LocalDate.now()
        val rates = mapOf(
            Currency.getInstance("CHF") to TreeMap(
                mapOf(
                    now to 1.0,
                    now.plusDays(1) to 2.0,
                )
            )
        )

        val subject = UsdExchangeRateReader(rates)

        // When
        val result = subject.getRate(Currency.getInstance("EUR"), now)

        // Then
        expectThat(result).isNull()
    }

    @Test
    fun `getExchangeCurrencies returns all currencies for which there is exchange data`() {
        // Given
        val chf = Currency.getInstance("CHF")
        val eur = Currency.getInstance("EUR")
        val usd = Currency.getInstance("USD")
        val rates = mapOf(
            chf to TreeMap<LocalDate, Double>(),
            eur to TreeMap<LocalDate, Double>(),
            usd to TreeMap<LocalDate, Double>(),
        )

        val subject = UsdExchangeRateReader(rates)

        // When
        val result = subject.getExchangeCurrencies()

        // Then
        expectThat(result).isEqualTo(setOf(chf, eur, usd))
    }
}
