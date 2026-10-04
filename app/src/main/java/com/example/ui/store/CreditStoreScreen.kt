package com.example.ui.store

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CreditPackage
import com.example.data.models.DEFAULT_CREDIT_PACKAGES
import com.example.data.models.PaymentCard
import com.example.data.models.PerkCategory
import com.example.data.models.STORE_PERK_ITEMS
import com.example.data.models.StorePerkItem
import com.example.data.preferences.SettingsPreferences
import com.example.sound.SoundManager
import com.example.sound.VibrationManager
import com.example.ui.components.GamingButton
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.NeonGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditStoreScreen(
    preferences: SettingsPreferences,
    soundManager: SoundManager,
    vibrationManager: VibrationManager,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val creditBalance by preferences.creditBalanceFlow.collectAsState(initial = 250)
    val savedCards by preferences.savedCardsFlow.collectAsState(initial = emptyList())
    val unlockedPerks by preferences.unlockedPerksFlow.collectAsState(initial = emptySet())
    val revivePasses by preferences.revivePassesFlow.collectAsState(initial = 1)
    val doubleScorePasses by preferences.doubleScorePassesFlow.collectAsState(initial = 0)
    val currentSkin by preferences.snakeSkinFlow.collectAsState(initial = com.example.data.models.SnakeSkin.CLASSIC)

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("PACKAGES", "PERKS & SKINS", "SAVED CARDS")

    var showAddCardDialog by remember { mutableStateOf(false) }
    var selectedPackageForCheckout by remember { mutableStateOf<CreditPackage?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CREDIT STORE",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Glowing Coin Balance Pill
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = GoldContainer.copy(alpha = 0.8f),
                            border = BorderStroke(1.5.dp, GoldPrimary),
                            modifier = Modifier.testTag("store_credit_balance_pill")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$creditBalance",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = GoldPrimary
                                )
                            }
                        }
                    }

                    // Tab Row
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    ) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        text = title,
                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedTab) {
                0 -> {
                    // TAB 1: CREDIT PACKAGES
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CreditCard,
                                        contentDescription = null,
                                        tint = NeonGreen,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Buy Credits with Credit or Debit Card",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Add your card once to enjoy 1-tap checkout. Credits never expire.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        items(DEFAULT_CREDIT_PACKAGES) { pack ->
                            CreditPackageCard(
                                pack = pack,
                                onBuyClick = {
                                    selectedPackageForCheckout = pack
                                }
                            )
                        }
                    }
                }

                1 -> {
                    // TAB 2: PERKS & SKINS
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            // Inventory Summary Card
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Revive Shields", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("$revivePasses", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Score Doublers", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("$doubleScorePasses", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Skins Unlocked", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("${unlockedPerks.count { it.startsWith("perk_skin") }} / 3", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }

                        items(STORE_PERK_ITEMS) { perk ->
                            val isUnlocked = unlockedPerks.contains(perk.id)
                            val isEquipped = perk.associatedSkin != null && currentSkin == perk.associatedSkin

                            PerkItemCard(
                                perk = perk,
                                isUnlocked = isUnlocked,
                                isEquipped = isEquipped,
                                userCredits = creditBalance,
                                onRedeem = {
                                    scope.launch {
                                        if (creditBalance < perk.costCredits) {
                                            snackbarHostState.showSnackbar("Not enough credits! Purchase a credit package first.")
                                            return@launch
                                        }

                                        val success = preferences.spendCredits(perk.costCredits)
                                        if (success) {
                                            soundManager.playPurchaseSuccess()
                                            vibrationManager.vibrateLevelUp()

                                            when (perk.id) {
                                                "perk_revive_shields_3" -> preferences.addReviveShields(3)
                                                "perk_double_score_5" -> preferences.addDoubleScorePasses(5)
                                                else -> {
                                                    preferences.unlockPerk(perk.id)
                                                    if (perk.associatedSkin != null) {
                                                        preferences.setSnakeSkin(perk.associatedSkin)
                                                    }
                                                }
                                            }
                                            snackbarHostState.showSnackbar("Unlocked: ${perk.name}!")
                                        }
                                    }
                                },
                                onEquip = {
                                    if (perk.associatedSkin != null) {
                                        scope.launch {
                                            preferences.setSnakeSkin(perk.associatedSkin)
                                            soundManager.playButtonClick()
                                            snackbarHostState.showSnackbar("Equipped: ${perk.name}")
                                        }
                                    }
                                }
                            )
                        }
                    }
                }

                2 -> {
                    // TAB 3: SAVED CARDS
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Your Saved Cards",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Use for instant 1-tap credit package top-ups",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Button(
                                    onClick = { showAddCardDialog = true },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    modifier = Modifier.testTag("add_card_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Card", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (savedCards.isEmpty()) {
                            item {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 32.dp),
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(64.dp)
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CreditCard,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(36.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Text(
                                            text = "No Payment Cards Added Yet",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = "Add a credit or debit card to instantly purchase credits and unlock exclusive skins and perks.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center
                                        )

                                        Spacer(modifier = Modifier.height(20.dp))

                                        GamingButton(
                                            text = "ADD YOUR FIRST CARD",
                                            onClick = { showAddCardDialog = true },
                                            testTag = "empty_add_card_cta"
                                        )
                                    }
                                }
                            }
                        } else {
                            items(savedCards) { card ->
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    CreditCardView(
                                        card = card,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (!card.isDefault) {
                                            OutlinedButton(
                                                onClick = {
                                                    scope.launch {
                                                        preferences.setDefaultCard(card.id)
                                                        snackbarHostState.showSnackbar("Default card updated")
                                                    }
                                                },
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Text("Set as Default", fontSize = 12.sp)
                                            }
                                        } else {
                                            Text(
                                                text = "✓ Default Card for Top-ups",
                                                color = GoldPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                scope.launch {
                                                    preferences.deleteCard(card.id)
                                                    snackbarHostState.showSnackbar("Card removed")
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete Card",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Card Dialog
    if (showAddCardDialog) {
        AddCardDialog(
            onDismiss = { showAddCardDialog = false },
            onCardAdded = { newCard ->
                showAddCardDialog = false
                scope.launch {
                    preferences.saveCard(newCard)
                    soundManager.playButtonClick()
                    snackbarHostState.showSnackbar("Card successfully saved!")
                }
            }
        )
    }

    // Checkout Bottom Sheet
    selectedPackageForCheckout?.let { pack ->
        CheckoutBottomSheet(
            creditPackage = pack,
            savedCards = savedCards,
            onDismiss = { selectedPackageForCheckout = null },
            onAddNewCard = {
                selectedPackageForCheckout = null
                showAddCardDialog = true
            },
            onPaymentSuccess = { purchasedPack, cardUsed ->
                scope.launch {
                    preferences.addCredits(purchasedPack.totalCredits)
                    soundManager.playPurchaseSuccess()
                    vibrationManager.vibrateNewRecord()
                    snackbarHostState.showSnackbar(
                        "Success! Added ${purchasedPack.totalCredits} Credits with ${cardUsed.cardBrand.displayName} •••• ${cardUsed.last4}"
                    )
                }
            }
        )
    }
}

@Composable
fun CreditPackageCard(
    pack: CreditPackage,
    onBuyClick: () -> Unit
) {
    val isHighlighted = pack.isPopular || pack.isBestValue

    val borderStroke = when {
        pack.isBestValue -> BorderStroke(2.dp, GoldPrimary)
        pack.isPopular -> BorderStroke(2.dp, NeonGreen)
        else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    }

    val cardBg = if (isHighlighted) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onBuyClick() },
        shape = RoundedCornerShape(18.dp),
        color = cardBg,
        border = borderStroke
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Badge if any
            if (pack.tag != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (pack.isBestValue) GoldPrimary else if (pack.isPopular) NeonGreen else MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = pack.tag,
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                Brush.linearGradient(
                                    if (pack.isBestValue) listOf(GoldPrimary, GoldSecondary)
                                    else listOf(NeonGreen, Color(0xFF00B0FF))
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = pack.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${pack.credits} Credits",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary
                            )
                            if (pack.bonusCredits > 0) {
                                Text(
                                    text = " +${pack.bonusCredits} Free",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonGreen
                                )
                            }
                        }
                    }
                }

                // Buy Price Button
                Button(
                    onClick = onBuyClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (pack.isBestValue) GoldPrimary else MaterialTheme.colorScheme.primary,
                        contentColor = if (pack.isBestValue) Color.Black else MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.testTag("buy_pack_${pack.id}")
                ) {
                    Text(
                        text = pack.priceUsd,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PerkItemCard(
    perk: StorePerkItem,
    isUnlocked: Boolean,
    isEquipped: Boolean,
    userCredits: Int,
    onRedeem: () -> Unit,
    onEquip: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            when (perk.category) {
                                PerkCategory.SKIN -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                PerkCategory.POWERUP -> NeonGreen.copy(alpha = 0.2f)
                                PerkCategory.ARENA -> GoldPrimary.copy(alpha = 0.2f)
                            },
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (perk.category) {
                            PerkCategory.SKIN -> Icons.Default.Star
                            PerkCategory.POWERUP -> Icons.Default.Shield
                            PerkCategory.ARENA -> Icons.Default.ElectricBolt
                        },
                        contentDescription = null,
                        tint = when (perk.category) {
                            PerkCategory.SKIN -> MaterialTheme.colorScheme.primary
                            PerkCategory.POWERUP -> NeonGreen
                            PerkCategory.ARENA -> GoldPrimary
                        },
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = perk.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = perk.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${perk.costCredits} Credits",
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Action Button
            if (isEquipped) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = NeonGreen.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, NeonGreen)
                ) {
                    Text(
                        text = "EQUIPPED",
                        color = NeonGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            } else if (isUnlocked && !perk.isConsumable) {
                OutlinedButton(
                    onClick = onEquip,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("EQUIP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onRedeem,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (userCredits >= perk.costCredits) GoldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.testTag("redeem_${perk.id}")
                ) {
                    Text(
                        text = if (perk.isConsumable) "GET" else "UNLOCK",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
