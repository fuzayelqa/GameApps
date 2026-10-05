package com.example.utils

import com.example.data.models.CardBrand
import java.util.Calendar

sealed class CardValidationResult {
    object Valid : CardValidationResult()
    data class Invalid(val message: String) : CardValidationResult()
}

object CardValidator {

    /**
     * Real cards must pass the ISO/IEC 7812 Luhn (Mod-10) algorithm.
     * Fake cards, random numbers, and repeated digits will fail.
     */
    fun validateLuhn(cardNumber: String): Boolean {
        val digits = cardNumber.filter { it.isDigit() }
        if (digits.length !in 13..19) return false

        // Reject obvious fake repeating numbers (e.g. 0000000000000000, 1111111111111111)
        if (digits.all { it == digits[0] }) return false

        var sum = 0
        var alternate = false
        for (i in digits.length - 1 downTo 0) {
            var n = digits[i].digitToInt()
            if (alternate) {
                n *= 2
                if (n > 9) {
                    n -= 9
                }
            }
            sum += n
            alternate = !alternate
        }
        return (sum % 10 == 0)
    }

    /**
     * Checks if the card belongs to a recognized financial banking network (BIN/IIN).
     */
    fun detectAndValidateNetwork(cardNumber: String): Pair<CardBrand, Boolean> {
        val cleaned = cardNumber.filter { it.isDigit() }
        if (cleaned.length < 4) return Pair(CardBrand.GENERIC, false)

        val brand = CardBrand.detect(cleaned)
        val hasValidLength = when (brand) {
            CardBrand.VISA -> cleaned.length == 16 || cleaned.length == 13
            CardBrand.MASTERCARD -> cleaned.length == 16
            CardBrand.AMEX -> cleaned.length == 15
            CardBrand.DISCOVER -> cleaned.length == 16
            CardBrand.GENERIC -> false
        }

        return Pair(brand, hasValidLength && brand != CardBrand.GENERIC)
    }

    /**
     * Complete check for card number authenticity.
     */
    fun validateCardNumber(cardNumber: String): CardValidationResult {
        val cleaned = cardNumber.filter { it.isDigit() }
        if (cleaned.length < 13) {
            return CardValidationResult.Invalid("Card number is too short.")
        }
        if (cleaned.length > 19) {
            return CardValidationResult.Invalid("Card number is too long.")
        }

        val (brand, networkValid) = detectAndValidateNetwork(cleaned)
        if (!networkValid) {
            return CardValidationResult.Invalid(
                "Unrecognized card network. Only genuine Visa, Mastercard, American Express, and Discover cards are accepted."
            )
        }

        if (!validateLuhn(cleaned)) {
            return CardValidationResult.Invalid(
                "Declined: Fake card number detected. This number failed bank checksum verification (Luhn Mod-10)."
            )
        }

        return CardValidationResult.Valid
    }

    /**
     * Validates that the card is not expired.
     */
    fun validateExpiry(expiry: String): CardValidationResult {
        val parts = expiry.split("/").map { it.trim() }
        if (parts.size != 2 || parts[0].length != 2 || parts[1].length != 2) {
            return CardValidationResult.Invalid("Expiry date must be in MM/YY format.")
        }

        val month = parts[0].toIntOrNull()
        val yearPart = parts[1].toIntOrNull()

        if (month == null || month !in 1..12) {
            return CardValidationResult.Invalid("Invalid expiry month (must be 01 - 12).")
        }
        if (yearPart == null) {
            return CardValidationResult.Invalid("Invalid expiry year.")
        }

        val calendar = Calendar.getInstance()
        val currentYearTwoDigits = calendar.get(Calendar.YEAR) % 100
        val currentMonth = calendar.get(Calendar.MONTH) + 1

        if (yearPart < currentYearTwoDigits) {
            return CardValidationResult.Invalid("Card is expired (past year).")
        }
        if (yearPart == currentYearTwoDigits && month < currentMonth) {
            return CardValidationResult.Invalid("Card is expired (past month).")
        }
        if (yearPart > currentYearTwoDigits + 20) {
            return CardValidationResult.Invalid("Expiry date is too far in the future.")
        }

        return CardValidationResult.Valid
    }

    /**
     * Validates CVV based on card brand.
     */
    fun validateCvv(cvv: String, brand: CardBrand): CardValidationResult {
        val digits = cvv.filter { it.isDigit() }
        val requiredLength = if (brand == CardBrand.AMEX) 4 else 3
        if (digits.length != requiredLength) {
            return CardValidationResult.Invalid(
                if (brand == CardBrand.AMEX) "Amex requires a 4-digit CID." else "Card requires a 3-digit CVV."
            )
        }
        return CardValidationResult.Valid
    }

    /**
     * Validates cardholder name (must contain first and last name).
     */
    fun validateCardholderName(name: String): CardValidationResult {
        val trimmed = name.trim()
        if (trimmed.length < 3) {
            return CardValidationResult.Invalid("Please enter the full cardholder name.")
        }
        val words = trimmed.split(Regex("\\s+"))
        if (words.size < 2) {
            return CardValidationResult.Invalid("Please enter both First and Last name as on the card.")
        }
        if (trimmed.any { it.isDigit() }) {
            return CardValidationResult.Invalid("Cardholder name cannot contain numbers.")
        }
        return CardValidationResult.Valid
    }
}
