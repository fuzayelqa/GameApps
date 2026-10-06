package com.example.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.AuthManager
import com.example.auth.AuthState
import com.example.billing.BillingManager
import com.example.data.preferences.SettingsPreferences
import com.example.ui.components.AdBannerView
import com.example.ui.components.GamingButton
import com.example.ui.components.GamingCard
import com.example.ui.components.GoldPremiumBadge
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonGreen

@Composable
fun HomeScreen(
    highScore: Int,
    authManager: AuthManager,
    billingManager: BillingManager,
    preferences: SettingsPreferences,
    onPlayClick: () -> Unit,
    onStoreClick: () -> Unit,
    onPremiumClick: () -> Unit,
    onRecordsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    val authState by authManager.authState.collectAsState()
    val isPremium by billingManager.isPremium.collectAsState()
    val creditBalance by preferences.creditBalanceFlow.collectAsState(initial = 250)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AdBannerView(isPremium = isPremium)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top User Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Auth Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.clickable {
                        if (authState !is AuthState.Authenticated) {
                            onLoginClick()
                        } else {
                            onSettingsClick()
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (authState is AuthState.Authenticated) NeonGreen else Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        when (val state = authState) {
                            is AuthState.Authenticated -> {
                                Text(
                                    text = state.user.email?.take(16)?.let { if (it.length >= 16) "$it..." else it } ?: "Player",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            else -> {
                                Text(
                                    text = "Playing as Guest",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Right Top: Credit Store Balance Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = GoldContainer.copy(alpha = 0.85f),
                    border = BorderStroke(1.5.dp, GoldPrimary),
                    modifier = Modifier
                        .clickable { onStoreClick() }
                        .testTag("home_credit_balance_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$creditBalance",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = GoldPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = NeonGreen
                        )
                    }
                }
            }

            // If not logged in, show quick sign-in banner
            if (authState !is AuthState.Authenticated) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clickable { onLoginClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sign in to save credits & records",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "SIGN IN",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Game Logo / Hero Title
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "SNAKE",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 4.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "GAME",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 8.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(20.dp))

                // High Score Card
                GamingCard(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    borderColor = NeonGreen.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "HIGH SCORE",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$highScore",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Actions Stack
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Primary Play Game Button
                GamingButton(
                    text = "PLAY GAME",
                    onClick = onPlayClick,
                    icon = Icons.Default.PlayArrow,
                    isPrimary = true,
                    modifier = Modifier
                        .height(60.dp)
                        .testTag("home_play_game_button")
                )

                // Credit Store Button with Gold Coin & Card Icon
                GamingButton(
                    text = "CREDIT STORE",
                    onClick = onStoreClick,
                    icon = Icons.Default.Storefront,
                    isGold = true,
                    isPrimary = false,
                    modifier = Modifier
                        .height(54.dp)
                        .testTag("home_store_button")
                )

                // Premium Button with Gold glow
                GamingButton(
                    text = if (isPremium) "PREMIUM UNLOCKED" else "PREMIUM NO-ADS",
                    onClick = onPremiumClick,
                    icon = Icons.Default.WorkspacePremium,
                    isGold = false,
                    isPrimary = false,
                    modifier = Modifier.testTag("home_premium_button")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Records Button
                    GamingButton(
                        text = "RECORDS",
                        onClick = onRecordsClick,
                        icon = Icons.Default.Leaderboard,
                        isPrimary = false,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_records_button")
                    )

                    // Settings Button
                    GamingButton(
                        text = "SETTINGS",
                        onClick = onSettingsClick,
                        icon = Icons.Default.Settings,
                        isPrimary = false,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_settings_button")
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
