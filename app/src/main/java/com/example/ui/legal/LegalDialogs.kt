package com.example.ui.legal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PrivacyPolicyDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Privacy Policy", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Snake Game (\"the Application\") values your privacy.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "1. Information We Collect:\n" +
                            "• Account Data: When you sign in with Google or create an account, we receive your email and user identifier through Firebase Authentication.\n" +
                            "• Game Data: Scores, levels reached, food collected, and game settings are stored locally on your device via Room Database and synchronized with Firebase Firestore when logged in.\n" +
                            "• Guest Mode: Guest users generate data that resides solely on their device until an account is optionally linked.\n" +
                            "• Advertising: Free users receive non-personalized or contextual ads powered by Google AdMob.\n\n" +
                            "2. How We Use Information:\n" +
                            "To provide gameplay, preserve your high scores across devices, process Premium subscriptions via Google Play Billing, and ensure system stability.\n\n" +
                            "3. Data Deletion:\n" +
                            "You may delete your account and associated cloud data at any time directly in the Settings menu.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("CLOSE")
            }
        }
    )
}

@Composable
fun TermsOfServiceDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Terms of Service", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Snake Game Terms and Conditions",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "1. Acceptance of Terms:\n" +
                            "By downloading, installing, or playing Snake Game, you agree to comply with and be bound by these terms.\n\n" +
                            "2. License and Conduct:\n" +
                            "Snake Game grants you a personal, non-transferable license for personal, non-commercial entertainment. Cheating, modifying game memory, or exploiting cloud sync is prohibited.\n\n" +
                            "3. Premium Subscriptions & Purchases:\n" +
                            "Premium skins, themes, and ad-removal are processed strictly through official Google Play In-App Billing. Subscriptions renew automatically unless cancelled via Google Play Store.\n\n" +
                            "4. Disclaimers:\n" +
                            "The game is provided \"as is\". We strive for 100% uptime and offline support, but cannot guarantee uninterrupted service on unsupported hardware.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("CLOSE")
            }
        }
    )
}
