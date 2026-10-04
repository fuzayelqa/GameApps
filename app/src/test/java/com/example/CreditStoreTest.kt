package com.example

import com.example.data.models.CardBrand
import com.example.data.models.DEFAULT_CREDIT_PACKAGES
import com.example.data.models.PaymentCard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CreditStoreTest {

    @Test
    fun testCardBrandDetection() {
        assertEquals(CardBrand.VISA, CardBrand.detect("4111 2222 3333 4444"))
        assertEquals(CardBrand.VISA, CardBrand.detect("4000123456789010"))
        assertEquals(CardBrand.MASTERCARD, CardBrand.detect("5105 1051 0510 5100"))
        assertEquals(CardBrand.MASTERCARD, CardBrand.detect("2223 0000 0000 0000"))
        assertEquals(CardBrand.AMEX, CardBrand.detect("3400 0000 0000 000"))
        assertEquals(CardBrand.AMEX, CardBrand.detect("3782 8224 6310 005"))
        assertEquals(CardBrand.DISCOVER, CardBrand.detect("6011 0000 0000 0000"))
        assertEquals(CardBrand.GENERIC, CardBrand.detect("9999 0000 0000 0000"))
    }

    @Test
    fun testCreditPackagesCalculations() {
        val packages = DEFAULT_CREDIT_PACKAGES
        assertTrue(packages.isNotEmpty())

        val diamondPack = packages.first { it.id == "pack_diamond_10000" }
        assertEquals(13500, diamondPack.totalCredits)
        assertTrue(diamondPack.isBestValue)

        val goldPack = packages.first { it.id == "pack_gold_4000" }
        assertEquals(5000, goldPack.totalCredits)
        assertTrue(goldPack.isPopular)
    }

    @Test
    fun testPaymentCardData() {
        val card = PaymentCard(
            id = "test_card_1",
            cardNumberMasked = "•••• •••• •••• 4242",
            last4 = "4242",
            cardHolderName = "Alex Mercer",
            expiryDate = "12/28",
            cardBrand = CardBrand.VISA,
            isDefault = true
        )

        assertEquals("4242", card.last4)
        assertEquals("Alex Mercer", card.cardHolderName)
        assertEquals("12/28", card.expiryDate)
        assertEquals(CardBrand.VISA, card.cardBrand)
        assertTrue(card.isDefault)
    }
}
