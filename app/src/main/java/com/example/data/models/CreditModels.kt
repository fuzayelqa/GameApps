package com.example.data.models

import java.util.UUID

enum class CardBrand(val displayName: String) {
    VISA("Visa"),
    MASTERCARD("Mastercard"),
    AMEX("American Express"),
    DISCOVER("Discover"),
    GENERIC("Card");

    companion object {
        fun detect(cardNumber: String): CardBrand {
            val cleaned = cardNumber.replace(" ", "").replace("-", "")
            return when {
                cleaned.startsWith("4") -> VISA
                cleaned.matches(Regex("^(5[1-5]|2[2-7]).*")) -> MASTERCARD
                cleaned.matches(Regex("^(34|37).*")) -> AMEX
                cleaned.matches(Regex("^(6011|65).*")) -> DISCOVER
                else -> GENERIC
            }
        }
    }
}

data class PaymentCard(
    val id: String = UUID.randomUUID().toString(),
    val cardNumberMasked: String = "•••• •••• •••• 0000",
    val last4: String = "0000",
    val cardHolderName: String = "",
    val expiryDate: String = "",
    val cardBrand: CardBrand = CardBrand.VISA,
    val cardGradientStart: Long = 0xFF1E3A8A, // Deep Navy / Royal
    val cardGradientEnd: Long = 0xFF3B82F6,   // Blue
    val isDefault: Boolean = false
)

data class CreditPackage(
    val id: String,
    val name: String,
    val credits: Int,
    val bonusCredits: Int = 0,
    val priceUsd: String,
    val priceValue: Double,
    val isPopular: Boolean = false,
    val isBestValue: Boolean = false,
    val tag: String? = null
) {
    val totalCredits: Int get() = credits + bonusCredits
}

enum class PerkCategory {
    SKIN,
    POWERUP,
    ARENA
}

data class StorePerkItem(
    val id: String,
    val name: String,
    val description: String,
    val costCredits: Int,
    val category: PerkCategory,
    val isConsumable: Boolean = false,
    val associatedSkin: SnakeSkin? = null
)

val DEFAULT_CREDIT_PACKAGES = listOf(
    CreditPackage(
        id = "pack_100",
        name = "100 CREDITS",
        credits = 100,
        bonusCredits = 0,
        priceUsd = "$1.01",
        priceValue = 1.01
    ),
    CreditPackage(
        id = "pack_200",
        name = "200 CREDITS",
        credits = 200,
        bonusCredits = 0,
        priceUsd = "$2.01",
        priceValue = 2.01
    ),
    CreditPackage(
        id = "pack_300",
        name = "300 CREDITS",
        credits = 300,
        bonusCredits = 0,
        priceUsd = "$3.01",
        priceValue = 3.01
    ),
    CreditPackage(
        id = "pack_400",
        name = "400 CREDITS",
        credits = 400,
        bonusCredits = 0,
        priceUsd = "$4.01",
        priceValue = 4.01
    ),
    CreditPackage(
        id = "pack_500",
        name = "500 CREDITS",
        credits = 500,
        bonusCredits = 0,
        priceUsd = "$5.01",
        priceValue = 5.01
    ),
    CreditPackage(
        id = "pack_1000",
        name = "1000 CREDITS",
        credits = 1000,
        bonusCredits = 0,
        priceUsd = "$10.01",
        priceValue = 10.01
    ),
    CreditPackage(
        id = "pack_1500",
        name = "1500 CREDITS",
        credits = 1500,
        bonusCredits = 0,
        priceUsd = "$15.01",
        priceValue = 15.01
    ),
    CreditPackage(
        id = "pack_2000",
        name = "2000 CREDITS",
        credits = 2000,
        bonusCredits = 0,
        priceUsd = "$20.01",
        priceValue = 20.01
    ),
    CreditPackage(
        id = "pack_3000",
        name = "3000 CREDITS",
        credits = 3000,
        bonusCredits = 0,
        priceUsd = "$30.01",
        priceValue = 30.01
    ),
    CreditPackage(
        id = "pack_4000",
        name = "4000 CREDITS",
        credits = 4000,
        bonusCredits = 0,
        priceUsd = "$40.01",
        priceValue = 40.01
    ),
    CreditPackage(
        id = "pack_5000",
        name = "5000 CREDITS",
        credits = 5000,
        bonusCredits = 0,
        priceUsd = "$50.01",
        priceValue = 50.01
    ),
    CreditPackage(
        id = "pack_10000",
        name = "10000 CREDITS",
        credits = 10000,
        bonusCredits = 0,
        priceUsd = "$99.99",
        priceValue = 99.99,
        isBestValue = true,
        tag = "WHALE"
    )
)

val STORE_PERK_ITEMS = listOf(
    StorePerkItem(
        id = "perk_revive_shields_3",
        name = "3x Revive Shields",
        description = "Continue your run instantly without losing score or progress.",
        costCredits = 300,
        category = PerkCategory.POWERUP,
        isConsumable = true
    ),
    StorePerkItem(
        id = "perk_double_score_5",
        name = "5x Score Doublers",
        description = "Earn 2x points per fruit collected for 30 seconds per run.",
        costCredits = 450,
        category = PerkCategory.POWERUP,
        isConsumable = true
    ),
    StorePerkItem(
        id = "perk_skin_cyber_viper",
        name = "Cyber Viper Skin",
        description = "High-tech neon cyan plating with glowing digital pulse trails.",
        costCredits = 800,
        category = PerkCategory.SKIN,
        isConsumable = false,
        associatedSkin = SnakeSkin.NEON
    ),
    StorePerkItem(
        id = "perk_skin_golden_emperor",
        name = "Golden Dragon Skin",
        description = "Shimmering 24K pure gold scales for high roller champions.",
        costCredits = 1500,
        category = PerkCategory.SKIN,
        isConsumable = false,
        associatedSkin = SnakeSkin.GOLD
    ),
    StorePerkItem(
        id = "perk_skin_phantom",
        name = "Phantom Galaxy Skin",
        description = "Cosmic stellar specter snake with deep space aura effects.",
        costCredits = 1000,
        category = PerkCategory.SKIN,
        isConsumable = false,
        associatedSkin = SnakeSkin.GALAXY
    )
)
