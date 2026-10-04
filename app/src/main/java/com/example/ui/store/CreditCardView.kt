package com.example.ui.store

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CardBrand
import com.example.data.models.PaymentCard
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonGreen

@Composable
fun CreditCardView(
    card: PaymentCard,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    val gradientColors = when (card.cardBrand) {
        CardBrand.VISA -> listOf(Color(0xFF0D253F), Color(0xFF1B4965), Color(0xFF2E6F95))
        CardBrand.MASTERCARD -> listOf(Color(0xFF2B0938), Color(0xFF4A154B), Color(0xFF6B1D58))
        CardBrand.AMEX -> listOf(Color(0xFF0F3D3E), Color(0xFF104F55), Color(0xFF32746D))
        CardBrand.DISCOVER -> listOf(Color(0xFF43281C), Color(0xFF683B11), Color(0xFF8B5A2B))
        CardBrand.GENERIC -> listOf(Color(card.cardGradientStart), Color(card.cardGradientEnd))
    }

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) NeonGreen else if (card.isDefault) GoldPrimary.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.15f),
        label = "cardBorderColor"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .shadow(12.dp, RoundedCornerShape(18.dp), ambientColor = Color.Black, spotColor = Color.Black)
            .border(2.dp, borderColor, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradientColors))
                .padding(20.dp)
        ) {
            // Subtle ambient shine circle
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .align(Alignment.TopEnd)
                    .background(Color.White.copy(alpha = 0.04f), CircleShape)
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: Brand & Chip / Contactless
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Gold EMV Chip
                        Box(
                            modifier = Modifier
                                .width(38.dp)
                                .height(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFFFFDF7A), Color(0xFFD4AF37))))
                                .border(1.dp, Color(0xFF996515), RoundedCornerShape(6.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            imageVector = Icons.Default.Contactless,
                            contentDescription = "Contactless",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (card.isDefault) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GoldPrimary.copy(alpha = 0.25f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(
                                    text = "DEFAULT",
                                    color = GoldPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Brand Label
                        Text(
                            text = card.cardBrand.displayName.uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Middle: Card Number (Masked)
                Text(
                    text = card.cardNumberMasked,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )

                // Bottom Row: Holder Name & Expiry
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "CARDHOLDER",
                            color = Color.White.copy(alpha = 0.55f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = if (card.cardHolderName.isNotBlank()) card.cardHolderName.uppercase() else "CARD HOLDER",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "EXPIRES",
                            color = Color.White.copy(alpha = 0.55f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = if (card.expiryDate.isNotBlank()) card.expiryDate else "MM/YY",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
