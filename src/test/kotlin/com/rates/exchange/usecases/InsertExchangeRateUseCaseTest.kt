package com.rates.exchange.usecases

import com.rates.exchange.domain.ExchangeRate
import com.rates.exchange.exceptions.CannotModifyExchangeRateException
import com.rates.exchange.exceptions.UnsupportedBaseCurrencyException
import com.rates.exchange.persistence.UsdExchangeRateReader
import com.rates.exchange.persistence.UsdExchangeRateWriter
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThrows
import java.time.LocalDate
import java.util.Currency

@ExtendWith(MockKExtension::class)
internal class InsertExchangeRateUseCaseTest {

    @MockK(relaxed = true)
    lateinit var usdExchangeRateReader: UsdExchangeRateReader

    @MockK(relaxed = true)
    lateinit var usdExchangeRateWriter: UsdExchangeRateWriter

    @InjectMockKs
    lateinit var subject: InsertExchangeRateUseCase

    @Test
    fun `insert - When base currency is not USD, Then UnsupportedBaseCurrencyException is thrown`() {

        // Given
        val base = Currency.getInstance("EUR")
        val currency = Currency.getInstance("CHF")
        val date = LocalDate.now()
        val rate = 2.0

        // When
        val insert = { subject.insert(base = base, currency = currency, date = date, rate = rate) }

        // Then
        expectThrows<UnsupportedBaseCurrencyException>(insert)
    }

    @Test
    fun `insert - When date already contains data and rate is the same, Then nothing happens`() {

        // Given
        val base = Currency.getInstance("USD")
        val currency = Currency.getInstance("CHF")
        val date = LocalDate.now()
        val rate = 2.0

        every { usdExchangeRateReader.getRate(any(), any()) } returns ExchangeRate(date, rate)

        // When
        subject.insert(base = base, currency = currency, date = date, rate = rate)

        // Then
        verify(exactly = 1) { usdExchangeRateReader.getRate(currency, date) }
        verify(exactly = 0) { usdExchangeRateWriter.insert(any(), any(), any()) }
        confirmVerified(
            usdExchangeRateReader,
            usdExchangeRateWriter,
        )
    }

    @Test
    fun `insert - When date already contains data and rate is different, Then CannotModifyExchangeRateException is thrown`() {

        // Given
        val base = Currency.getInstance("USD")
        val currency = Currency.getInstance("CHF")
        val date = LocalDate.now()
        val rate = 2.0

        every { usdExchangeRateReader.getRate(any(), any()) } returns ExchangeRate(date, rate)

        // When
        val insert = { subject.insert(base = base, currency = currency, date = date, rate = rate + 1) }

        // Then
        expectThrows<CannotModifyExchangeRateException>(insert)
    }

    @Test
    fun `insert - When no exchange rate exists for the day, Then it is inserted`() {

        // Given
        val base = Currency.getInstance("USD")
        val currency = Currency.getInstance("CHF")
        val date = LocalDate.now()
        val rate = 2.0

        every { usdExchangeRateReader.getRate(any(), any()) } returns null

        // When
        subject.insert(base = base, currency = currency, date = date, rate = rate)

        // Then
        verify(exactly = 1) { usdExchangeRateReader.getRate(currency, date) }
        verify(exactly = 1) { usdExchangeRateWriter.insert(currency, date, rate) }
        confirmVerified(
            usdExchangeRateReader,
            usdExchangeRateWriter,
        )
    }
}
