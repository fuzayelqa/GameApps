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
        id = "pack_starter_500",
        name = "Starter Pouch",
        credits = 500,
        bonusCredits = 0,
        priceUsd = "$1.99",
        priceValue = 1.99,
        tag = "BEGINNER"
    ),
    CreditPackage(
        id = "pack_silver_1500",
        name = "Silver Vault",
        credits = 1500,
        bonusCredits = 250,
        priceUsd = "$4.99",
        priceValue = 4.99,
        tag = "+15% BONUS"
    ),
    CreditPackage(
        id = "pack_gold_4000",
        name = "Golden Treasury",
        credits = 4000,
        bonusCredits = 1000,
        priceUsd = "$9.99",
        priceValue = 9.99,
        isPopular = true,
        tag = "MOST POPULAR"
    ),
    CreditPackage(
        id = "pack_diamond_10000",
        name = "Diamond Trove",
        credits = 10000,
        bonusCredits = 3500,
        priceUsd = "$19.99",
        priceValue = 19.99,
        isBestValue = true,
        tag = "BEST VALUE (+35%)"
    ),
    CreditPackage(
        id = "pack_mythic_25000",
        name = "Mythic Dragon Hoard",
        credits = 25000,
        bonusCredits = 12000,
        priceUsd = "$39.99",
        priceValue = 39.99,
        tag = "WHALE PACK (+48%)"
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
