package com.example.ui.settings

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.AuthManager
import com.example.auth.AuthState
import com.example.billing.BillingManager
import com.example.data.models.BoardStyle
import com.example.data.models.ControlType
import com.example.data.models.Difficulty
import com.example.data.models.SnakeSkin
import com.example.data.models.ThemeType
import com.example.data.preferences.SettingsPreferences
import com.example.ui.components.AdBannerView
import com.example.ui.components.GamingButton
import com.example.ui.components.GamingCard
import com.example.ui.legal.PrivacyPolicyDialog
import com.example.ui.legal.TermsOfServiceDialog
import com.example.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    preferences: SettingsPreferences,
    authManager: AuthManager,
    billingManager: BillingManager,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToPremium: () -> Unit,
    onNavigateToStore: () -> Unit,
    onResetRecords: () -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val creditBalance by preferences.creditBalanceFlow.collectAsState(initial = 250)

    val controlType by preferences.controlTypeFlow.collectAsState(initial = ControlType.SWIPE)
    val difficulty by preferences.difficultyFlow.collectAsState(initial = Difficulty.NORMAL)
    val vibration by preferences.vibrationFlow.collectAsState(initial = true)
    val sound by preferences.soundFlow.collectAsState(initial = true)
    val music by preferences.musicFlow.collectAsState(initial = true)
    val currentTheme by preferences.themeFlow.collectAsState(initial = ThemeType.DARK)
    val currentSkin by preferences.snakeSkinFlow.collectAsState(initial = SnakeSkin.CLASSIC)
    val currentBoardStyle by preferences.boardStyleFlow.collectAsState(initial = BoardStyle.GRID)

    val isPremium by billingManager.isPremium.collectAsState()
    val authState by authManager.authState.collectAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showResetRecordsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AdBannerView(isPremium = isPremium)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SETTINGS",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // 1. GAMEPLAY SECTION
            item {
                SectionTitle("GAMEPLAY")
            }

            item {
                GamingCard {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Control Type Selector
                        Text(
                            text = "Control Type",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ControlType.entries.forEach { type ->
                                val isSelected = controlType == type
                                OutlinedButton(
                                    onClick = { scope.launch { preferences.setControlType(type) } },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                    )
                                ) {
                                    Text(
                                        text = type.displayName,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        // Difficulty Selector
                        Text(
                            text = "Difficulty",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Difficulty.entries.forEach { diff ->
                                val isSelected = difficulty == diff
                                OutlinedButton(
                                    onClick = { scope.launch { preferences.setDifficulty(diff) } },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                    )
                                ) {
                                    Text(
                                        text = diff.displayName,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        // Switches: Vibration, Sound, Music
                        SettingSwitchRow(
                            label = "Vibration",
                            checked = vibration,
                            onCheckedChange = { scope.launch { preferences.setVibration(it) } }
                        )
                        SettingSwitchRow(
                            label = "Sound Effects",
                            checked = sound,
                            onCheckedChange = { scope.launch { preferences.setSound(it) } }
                        )
                        SettingSwitchRow(
                            label = "Background Music",
                            checked = music,
                            onCheckedChange = { scope.launch { preferences.setMusic(it) } }
                        )
                    }
                }
            }

            // 2. APPEARANCE SECTION
            item {
                SectionTitle("APPEARANCE")
            }

            item {
                GamingCard {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Themes
                        Text(
                            text = "Theme",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            ThemeType.entries.forEach { theme ->
                                val isSelected = currentTheme == theme
                                val isLocked = theme.isPremium && !isPremium
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else Color.Transparent)
                                        .clickable {
                                            if (isLocked) {
                                                onNavigateToPremium()
                                            } else {
                                                scope.launch { preferences.setTheme(theme) }
                                            }
                                        }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = theme.displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isLocked) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Lock, contentDescription = "Locked", tint = GoldPrimary, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Premium", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Snake Skins
                        Text(
                            text = "Snake Skin",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SnakeSkin.entries.forEach { skin ->
                                val isSelected = currentSkin == skin
                                val isLocked = skin.isPremium && !isPremium
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else Color.Transparent)
                                        .clickable {
                                            if (isLocked) {
                                                onNavigateToPremium()
                                            } else {
                                                scope.launch { preferences.setSnakeSkin(skin) }
                                            }
                                        }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clip(CircleShape)
                                                .background(Color(skin.hexColor))
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = skin.displayName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    if (isLocked) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Lock, contentDescription = "Locked", tint = GoldPrimary, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Premium", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Board Style
                        Text(
                            text = "Board Style",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            BoardStyle.entries.forEach { style ->
                                val isSelected = currentBoardStyle == style
                                OutlinedButton(
                                    onClick = { scope.launch { preferences.setBoardStyle(style) } },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                    )
                                ) {
                                    Text(
                                        text = style.displayName,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. ACCOUNT SECTION
            item {
                SectionTitle("ACCOUNT")
            }

            item {
                GamingCard {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        when (val state = authState) {
                            is AuthState.Authenticated -> {
                                Text(
                                    text = "Email: ${state.user.email ?: "Signed in user"}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Status: ",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (state.user.isEmailVerified) {
                                        Icon(Icons.Default.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Verified", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    } else {
                                        Text("Unverified", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { showLogoutDialog = true },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("LOG OUT", color = MaterialTheme.colorScheme.onSurface)
                                    }
                                    OutlinedButton(
                                        onClick = { showDeleteAccountDialog = true },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.error
                                        )
                                    ) {
                                        Text("DELETE ACCOUNT", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                                    }
                                }
                            }
                            else -> {
                                Text(
                                    text = "You are currently playing as a guest. Sign in with Google to protect your records and sync progress across devices.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    GamingButton(
                                        text = "SIGN IN WITH GOOGLE",
                                        onClick = onNavigateToLogin,
                                        modifier = Modifier.fillMaxWidth(),
                                        testTag = "settings_google_sign_in_button"
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // STORE & PAYMENTS SECTION
            item {
                SectionTitle("STORE & PAYMENTS")
            }

            item {
                GamingCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Credit Balance",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "For skins, revives, and powerups",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "$creditBalance Credits",
                                color = GoldPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                        }

                        GamingButton(
                            text = "CREDIT STORE & CARDS",
                            onClick = onNavigateToStore,
                            isGold = true,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "settings_open_store_button"
                        )
                    }
                }
            }

            // 4. DATA SECTION
            item {
                SectionTitle("DATA")
            }

            item {
                GamingCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = { showResetRecordsDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Reset Local Records", color = MaterialTheme.colorScheme.error)
                        }

                        OutlinedButton(
                            onClick = {
                                billingManager.restorePurchases { restored, msg ->
                                    scope.launch { snackbarHostState.showSnackbar(msg) }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Restore Purchases", color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            // 5. ABOUT SECTION
            item {
                SectionTitle("ABOUT")
            }

            item {
                GamingCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showPrivacyDialog = true }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Privacy Policy", style = MaterialTheme.typography.bodyMedium)
                            Text("›", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showTermsDialog = true }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Terms of Service", style = MaterialTheme.typography.bodyMedium)
                            Text("›", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("App Version", style = MaterialTheme.typography.bodyMedium)
                            Text("1.0 (Production)", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Dialogs
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Log Out") },
                text = { Text("Are you sure you want to log out? Your cloud data will remain safely stored.") },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutDialog = false
                        authManager.logout()
                    }) {
                        Text("LOG OUT", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("CANCEL")
                    }
                }
            )
        }

        if (showDeleteAccountDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteAccountDialog = false },
                title = { Text("Delete Account") },
                text = { Text("Deleting your account may permanently remove your cloud game data. This action cannot be undone.") },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteAccountDialog = false
                        scope.launch {
                            val res = authManager.deleteAccount()
                            if (res.isFailure) {
                                snackbarHostState.showSnackbar("Failed to delete account: ${res.exceptionOrNull()?.localizedMessage}")
                            } else {
                                snackbarHostState.showSnackbar("Account deleted successfully.")
                            }
                        }
                    }) {
                        Text("DELETE", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteAccountDialog = false }) {
                        Text("CANCEL")
                    }
                }
            )
        }

        if (showResetRecordsDialog) {
            AlertDialog(
                onDismissRequest = { showResetRecordsDialog = false },
                title = { Text("Reset Records") },
                text = { Text("Are you sure you want to clear your local game history and records?") },
                confirmButton = {
                    TextButton(onClick = {
                        showResetRecordsDialog = false
                        onResetRecords()
                        scope.launch { snackbarHostState.showSnackbar("Records have been reset.") }
                    }) {
                        Text("RESET", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetRecordsDialog = false }) {
                        Text("CANCEL")
                    }
                }
            )
        }

        if (showPrivacyDialog) {
            PrivacyPolicyDialog(onDismiss = { showPrivacyDialog = false })
        }

        if (showTermsDialog) {
            TermsOfServiceDialog(onDismiss = { showTermsDialog = false })
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun SettingSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}
