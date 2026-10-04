package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.models.BoardStyle
import com.example.data.models.CardBrand
import com.example.data.models.ControlType
import com.example.data.models.Difficulty
import com.example.data.models.PaymentCard
import com.example.data.models.SnakeSkin
import com.example.data.models.ThemeType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "snake_settings")

class SettingsPreferences(private val context: Context) {

    companion object {
        val KEY_CONTROL_TYPE = stringPreferencesKey("control_type")
        val KEY_DIFFICULTY = stringPreferencesKey("difficulty")
        val KEY_VIBRATION = booleanPreferencesKey("vibration")
        val KEY_SOUND = booleanPreferencesKey("sound")
        val KEY_MUSIC = booleanPreferencesKey("music")
        val KEY_THEME = stringPreferencesKey("theme")
        val KEY_SNAKE_SKIN = stringPreferencesKey("snake_skin")
        val KEY_BOARD_STYLE = stringPreferencesKey("board_style")
        val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val KEY_IS_PREMIUM_CACHED = booleanPreferencesKey("is_premium_cached")

        // Credit Store & Cards Keys
        val KEY_CREDIT_BALANCE = intPreferencesKey("credit_balance")
        val KEY_SAVED_CARDS_JSON = stringPreferencesKey("saved_cards_json")
        val KEY_UNLOCKED_PERKS = stringSetPreferencesKey("unlocked_perks")
        val KEY_REVIVE_PASSES = intPreferencesKey("revive_passes")
        val KEY_DOUBLE_SCORE_PASSES = intPreferencesKey("double_score_passes")
    }

    val controlTypeFlow: Flow<ControlType> = context.dataStore.data.map { prefs ->
        val name = prefs[KEY_CONTROL_TYPE] ?: ControlType.SWIPE.name
        runCatching { ControlType.valueOf(name) }.getOrDefault(ControlType.SWIPE)
    }

    val difficultyFlow: Flow<Difficulty> = context.dataStore.data.map { prefs ->
        val name = prefs[KEY_DIFFICULTY] ?: Difficulty.NORMAL.name
        runCatching { Difficulty.valueOf(name) }.getOrDefault(Difficulty.NORMAL)
    }

    val vibrationFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_VIBRATION] ?: true
    }

    val soundFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_SOUND] ?: true
    }

    val musicFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_MUSIC] ?: true
    }

    val themeFlow: Flow<ThemeType> = context.dataStore.data.map { prefs ->
        val name = prefs[KEY_THEME] ?: ThemeType.DARK.name
        runCatching { ThemeType.valueOf(name) }.getOrDefault(ThemeType.DARK)
    }

    val snakeSkinFlow: Flow<SnakeSkin> = context.dataStore.data.map { prefs ->
        val name = prefs[KEY_SNAKE_SKIN] ?: SnakeSkin.CLASSIC.name
        runCatching { SnakeSkin.valueOf(name) }.getOrDefault(SnakeSkin.CLASSIC)
    }

    val boardStyleFlow: Flow<BoardStyle> = context.dataStore.data.map { prefs ->
        val name = prefs[KEY_BOARD_STYLE] ?: BoardStyle.GRID.name
        runCatching { BoardStyle.valueOf(name) }.getOrDefault(BoardStyle.GRID)
    }

    val onboardingCompletedFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_ONBOARDING_COMPLETED] ?: false
    }

    val isPremiumCachedFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_IS_PREMIUM_CACHED] ?: false
    }

    // Credits Flow (Default 250 initial welcome bonus credits!)
    val creditBalanceFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_CREDIT_BALANCE] ?: 250
    }

    // Revive Shields Flow (Default 1 free shield)
    val revivePassesFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_REVIVE_PASSES] ?: 1
    }

    // Double Score Boosters
    val doubleScorePassesFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_DOUBLE_SCORE_PASSES] ?: 0
    }

    // Unlocked Perks / Skins
    val unlockedPerksFlow: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[KEY_UNLOCKED_PERKS] ?: emptySet()
    }

    // Saved Payment Cards Flow
    val savedCardsFlow: Flow<List<PaymentCard>> = context.dataStore.data.map { prefs ->
        val jsonString = prefs[KEY_SAVED_CARDS_JSON] ?: ""
        if (jsonString.isBlank()) {
            emptyList()
        } else {
            try {
                val array = JSONArray(jsonString)
                val list = mutableListOf<PaymentCard>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        PaymentCard(
                            id = obj.optString("id"),
                            cardNumberMasked = obj.optString("cardNumberMasked"),
                            last4 = obj.optString("last4"),
                            cardHolderName = obj.optString("cardHolderName"),
                            expiryDate = obj.optString("expiryDate"),
                            cardBrand = runCatching { CardBrand.valueOf(obj.optString("cardBrand")) }.getOrDefault(CardBrand.VISA),
                            cardGradientStart = obj.optLong("cardGradientStart", 0xFF1E3A8A),
                            cardGradientEnd = obj.optLong("cardGradientEnd", 0xFF3B82F6),
                            isDefault = obj.optBoolean("isDefault", false)
                        )
                    )
                }
                list
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    suspend fun setControlType(type: ControlType) {
        context.dataStore.edit { it[KEY_CONTROL_TYPE] = type.name }
    }

    suspend fun setDifficulty(diff: Difficulty) {
        context.dataStore.edit { it[KEY_DIFFICULTY] = diff.name }
    }

    suspend fun setVibration(enabled: Boolean) {
        context.dataStore.edit { it[KEY_VIBRATION] = enabled }
    }

    suspend fun setSound(enabled: Boolean) {
        context.dataStore.edit { it[KEY_SOUND] = enabled }
    }

    suspend fun setMusic(enabled: Boolean) {
        context.dataStore.edit { it[KEY_MUSIC] = enabled }
    }

    suspend fun setTheme(theme: ThemeType) {
        context.dataStore.edit { it[KEY_THEME] = theme.name }
    }

    suspend fun setSnakeSkin(skin: SnakeSkin) {
        context.dataStore.edit { it[KEY_SNAKE_SKIN] = skin.name }
    }

    suspend fun setBoardStyle(style: BoardStyle) {
        context.dataStore.edit { it[KEY_BOARD_STYLE] = style.name }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[KEY_ONBOARDING_COMPLETED] = completed }
    }

    suspend fun setIsPremiumCached(isPremium: Boolean) {
        context.dataStore.edit { it[KEY_IS_PREMIUM_CACHED] = isPremium }
    }

    // Credits Management
    suspend fun addCredits(amount: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_CREDIT_BALANCE] ?: 250
            prefs[KEY_CREDIT_BALANCE] = current + amount
        }
    }

    suspend fun spendCredits(amount: Int): Boolean {
        var success = false
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_CREDIT_BALANCE] ?: 250
            if (current >= amount) {
                prefs[KEY_CREDIT_BALANCE] = current - amount
                success = true
            }
        }
        return success
    }

    suspend fun addReviveShields(count: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_REVIVE_PASSES] ?: 1
            prefs[KEY_REVIVE_PASSES] = current + count
        }
    }

    suspend fun useReviveShield(): Boolean {
        var used = false
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_REVIVE_PASSES] ?: 1
            if (current > 0) {
                prefs[KEY_REVIVE_PASSES] = current - 1
                used = true
            }
        }
        return used
    }

    suspend fun addDoubleScorePasses(count: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_DOUBLE_SCORE_PASSES] ?: 0
            prefs[KEY_DOUBLE_SCORE_PASSES] = current + count
        }
    }

    suspend fun unlockPerk(perkId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_UNLOCKED_PERKS] ?: emptySet()
            prefs[KEY_UNLOCKED_PERKS] = current + perkId
        }
    }

    // Card Management
    suspend fun saveCard(card: PaymentCard) {
        val currentList = savedCardsFlow.first().toMutableList()
        val existingIndex = currentList.indexOfFirst { it.id == card.id }
        
        // If this card is default or first card, ensure proper default states
        val isFirst = currentList.isEmpty()
        val willBeDefault = card.isDefault || isFirst

        val formattedCard = card.copy(isDefault = willBeDefault)

        if (willBeDefault) {
            // Unset default on all others
            for (i in 0 until currentList.size) {
                currentList[i] = currentList[i].copy(isDefault = false)
            }
        }

        if (existingIndex >= 0) {
            currentList[existingIndex] = formattedCard
        } else {
            currentList.add(formattedCard)
        }

        persistCards(currentList)
    }

    suspend fun deleteCard(cardId: String) {
        val currentList = savedCardsFlow.first().toMutableList()
        val wasRemoved = currentList.removeIf { it.id == cardId }
        if (wasRemoved && currentList.isNotEmpty() && currentList.none { it.isDefault }) {
            // Set first remaining card as default
            currentList[0] = currentList[0].copy(isDefault = true)
        }
        persistCards(currentList)
    }

    suspend fun setDefaultCard(cardId: String) {
        val currentList = savedCardsFlow.first().map {
            it.copy(isDefault = it.id == cardId)
        }
        persistCards(currentList)
    }

    private suspend fun persistCards(cards: List<PaymentCard>) {
        val array = JSONArray()
        for (c in cards) {
            val obj = JSONObject()
            obj.put("id", c.id)
            obj.put("cardNumberMasked", c.cardNumberMasked)
            obj.put("last4", c.last4)
            obj.put("cardHolderName", c.cardHolderName)
            obj.put("expiryDate", c.expiryDate)
            obj.put("cardBrand", c.cardBrand.name)
            obj.put("cardGradientStart", c.cardGradientStart)
            obj.put("cardGradientEnd", c.cardGradientEnd)
            obj.put("isDefault", c.isDefault)
            array.put(obj)
        }
        context.dataStore.edit { prefs ->
            prefs[KEY_SAVED_CARDS_JSON] = array.toString()
        }
    }
}
