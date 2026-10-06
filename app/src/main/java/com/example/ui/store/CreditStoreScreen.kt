package com.example.ui.store

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
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
import com.example.ui.components.CosmicStoreBackground
import com.example.ui.components.SciFiGold
import com.example.ui.components.SciFiNeonCyan
import com.example.ui.components.SciFiPackageCard
import com.example.ui.components.SciFiTechPanel
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

    var showAddCardDialog by remember { mutableStateOf(false) }
    var selectedPackageForCheckout by remember { mutableStateOf<CreditPackage?>(null) }
    var showPerksSheet by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Black
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 1. Cosmic Deep Space Background matching the uploaded design
            CosmicStoreBackground()

            // 2. Main Store Interface matching screenshot
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar with Optional Perks/Inventory shortcut
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { showPerksSheet = true },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("open_perks_sheet_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ColorLens,
                            contentDescription = "Skins & Perks",
                            tint = SciFiNeonCyan.copy(alpha = 0.85f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Header: CREDIT STORE (Bold Golden Yellow)
                Text(
                    text = "CREDIT STORE",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        shadow = Shadow(
                            color = Color(0xFFCC8800),
                            blurRadius = 14f
                        )
                    ),
                    color = Color(0xFFFFD000),
                    fontSize = 32.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Subtitle: YOUR CREDIT: 20 (Cyan glow)
                Text(
                    text = "YOUR CREDIT: $creditBalance",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        shadow = Shadow(
                            color = SciFiNeonCyan,
                            blurRadius = 16f
                        )
                    ),
                    color = SciFiNeonCyan,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("store_credit_balance_display")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 2-Column Grid of 12 Sci-Fi Credit Packages
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(DEFAULT_CREDIT_PACKAGES) { pack ->
                        val isSpecial = pack.id == "pack_10000"
                        SciFiPackageCard(
                            creditsText = pack.name,
                            priceText = pack.priceUsd,
                            isSpecial = isSpecial,
                            onClick = {
                                soundManager.playButtonClick()
                                selectedPackageForCheckout = pack
                            },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "buy_pack_${pack.id}"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Bottom BACK Button matching screenshot
                SciFiTechPanel(
                    modifier = Modifier
                        .width(160.dp)
                        .height(48.dp)
                        .padding(bottom = 6.dp),
                    onClick = {
                        soundManager.playButtonClick()
                        onBack()
                    },
                    testTag = "store_back_button"
                ) {
                    Text(
                        text = "BACK",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
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
                    snackbarHostState.showSnackbar("Payment card successfully added!")
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
                        "Added ${purchasedPack.totalCredits} Credits! (Charged to •••• ${cardUsed.last4})"
                    )
                }
            }
        )
    }

    // Perks & Skins Sheet (Access skins and powerups)
    if (showPerksSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPerksSheet = false },
            containerColor = Color(0xFF07111E),
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 36.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SKINS & PERKS",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = SciFiNeonCyan
                    )
                    IconButton(onClick = { showPerksSheet = false }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Text(
                    text = "Your Balance: $creditBalance Credits",
                    color = SciFiGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
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
        color = Color(0x660B1C30),
        border = BorderStroke(1.dp, SciFiNeonCyan.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            when (perk.category) {
                                PerkCategory.SKIN -> SciFiNeonCyan.copy(alpha = 0.2f)
                                PerkCategory.POWERUP -> NeonGreen.copy(alpha = 0.2f)
                                PerkCategory.ARENA -> SciFiGold.copy(alpha = 0.2f)
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
                            PerkCategory.SKIN -> SciFiNeonCyan
                            PerkCategory.POWERUP -> NeonGreen
                            PerkCategory.ARENA -> SciFiGold
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
                        color = Color.White
                    )
                    Text(
                        text = perk.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFA0B4C8),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = SciFiGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${perk.costCredits} Credits",
                            color = SciFiGold,
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
                    Text("EQUIP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SciFiNeonCyan)
                }
            } else {
                Button(
                    onClick = onRedeem,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (userCredits >= perk.costCredits) SciFiGold else Color.Gray.copy(alpha = 0.3f),
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
