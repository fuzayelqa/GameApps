package com.example.ui.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.data.models.CardBrand
import com.example.data.models.PaymentCard
import com.example.ui.components.GamingButton
import com.example.ui.theme.NeonGreen
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardDialog(
    onDismiss: () -> Unit,
    onCardAdded: (PaymentCard) -> Unit
) {
    var rawCardNumber by remember { mutableStateOf("") }
    var cardHolderName by remember { mutableStateOf("") }
    var rawExpiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var setAsDefault by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val detectedBrand = remember(rawCardNumber) {
        CardBrand.detect(rawCardNumber)
    }

    // Format card number with spaces for display: XXXX XXXX XXXX XXXX
    val formattedDisplayNumber = remember(rawCardNumber) {
        val digits = rawCardNumber.filter { it.isDigit() }.take(16)
        digits.chunked(4).joinToString(" ")
    }

    // Masked number for preview on card
    val maskedPreview = remember(rawCardNumber) {
        val digits = rawCardNumber.filter { it.isDigit() }
        if (digits.isEmpty()) {
            "•••• •••• •••• ••••"
        } else {
            val chunks = mutableListOf<String>()
            val padLength = 16
            val padded = digits.padEnd(padLength, '•')
            for (i in 0 until 4) {
                val start = i * 4
                val end = start + 4
                val chunk = padded.substring(start, end)
                if (i < 3) {
                    chunks.add(chunk.map { if (it.isDigit()) '•' else it }.joinToString(""))
                } else {
                    chunks.add(chunk)
                }
            }
            chunks.joinToString(" ")
        }
    }

    val previewCard = PaymentCard(
        id = "preview",
        cardNumberMasked = maskedPreview,
        last4 = rawCardNumber.filter { it.isDigit() }.takeLast(4).let { if (it.length == 4) it else "••••" },
        cardHolderName = cardHolderName,
        expiryDate = rawExpiry,
        cardBrand = detectedBrand,
        isDefault = setAsDefault
    )

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ADD PAYMENT CARD",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Encrypted & stored securely on your device",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Live Interactive Visual Card Preview
                CreditCardView(
                    card = previewCard,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // 1. Card Number Field
                OutlinedTextField(
                    value = formattedDisplayNumber,
                    onValueChange = { input ->
                        val digitsOnly = input.filter { it.isDigit() }.take(16)
                        rawCardNumber = digitsOnly
                        errorMessage = null
                    },
                    label = { Text("Card Number") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.CreditCard, contentDescription = null)
                    },
                    trailingIcon = {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = detectedBrand.displayName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_card_number_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Cardholder Name Field
                OutlinedTextField(
                    value = cardHolderName,
                    onValueChange = {
                        cardHolderName = it
                        errorMessage = null
                    },
                    label = { Text("Cardholder Name") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_card_holder_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Expiry and CVV Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = rawExpiry,
                        onValueChange = { input ->
                            val digits = input.filter { it.isDigit() }.take(4)
                            rawExpiry = if (digits.length >= 3) {
                                "${digits.substring(0, 2)}/${digits.substring(2)}"
                            } else {
                                digits
                            }
                            errorMessage = null
                        },
                        label = { Text("MM/YY") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_card_expiry_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    OutlinedTextField(
                        value = cvv,
                        onValueChange = { input ->
                            val digits = input.filter { it.isDigit() }.take(4)
                            cvv = digits
                            errorMessage = null
                        },
                        label = { Text("CVV / CVC") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                        },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_card_cvv_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Default Card Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Set as default payment card",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = setAsDefault,
                        onCheckedChange = { setAsDefault = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }

                // Security note
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = NeonGreen,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(
                        text = "256-bit SSL encrypted. Card numbers are tokenized and masked.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("CANCEL", color = MaterialTheme.colorScheme.onSurface)
                    }

                    Button(
                        onClick = {
                            val digits = rawCardNumber.filter { it.isDigit() }
                            if (digits.length < 15) {
                                errorMessage = "Please enter a valid 15 or 16-digit card number."
                                return@Button
                            }
                            if (cardHolderName.trim().length < 3) {
                                errorMessage = "Please enter the cardholder's name."
                                return@Button
                            }
                            if (rawExpiry.length < 5 || !rawExpiry.contains("/")) {
                                errorMessage = "Please enter expiry date in MM/YY format."
                                return@Button
                            }
                            val parts = rawExpiry.split("/")
                            val month = parts.getOrNull(0)?.toIntOrNull() ?: 0
                            if (month !in 1..12) {
                                errorMessage = "Invalid expiry month (01 - 12)."
                                return@Button
                            }
                            if (cvv.length < 3) {
                                errorMessage = "Please enter a valid 3 or 4-digit CVV."
                                return@Button
                            }

                            val last4 = digits.takeLast(4)
                            val masked = "•••• •••• •••• $last4"

                            val newCard = PaymentCard(
                                id = UUID.randomUUID().toString(),
                                cardNumberMasked = masked,
                                last4 = last4,
                                cardHolderName = cardHolderName.trim(),
                                expiryDate = rawExpiry,
                                cardBrand = detectedBrand,
                                isDefault = setAsDefault
                            )

                            onCardAdded(newCard)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_card_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("SAVE CARD", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
