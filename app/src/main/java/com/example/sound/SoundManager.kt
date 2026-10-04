package com.example.sound

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

private const val TAG = "SoundManager"

class SoundManager(private val context: Context) {

    private var toneGenerator: ToneGenerator? = null
    var isSoundEnabled: Boolean = true
    var isMusicEnabled: Boolean = true

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
        } catch (e: Exception) {
            Log.w(TAG, "ToneGenerator init failed", e)
        }
    }

    fun playButtonClick() {
        if (!isSoundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 30)
        } catch (e: Exception) {
            Log.w(TAG, "Play sound failed", e)
        }
    }

    fun playEatFood(isGolden: Boolean = false) {
        if (!isSoundEnabled) return
        try {
            if (isGolden) {
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 70)
            } else {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 50)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Play sound failed", e)
        }
    }

    fun playLevelUp() {
        if (!isSoundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 150)
        } catch (e: Exception) {
            Log.w(TAG, "Play sound failed", e)
        }
    }

    fun playGameOver() {
        if (!isSoundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ABBR_INTERCEPT, 300)
        } catch (e: Exception) {
            Log.w(TAG, "Play sound failed", e)
        }
    }

    fun playNewRecord() {
        if (!isSoundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_INCALL_LITE, 250)
        } catch (e: Exception) {
            Log.w(TAG, "Play sound failed", e)
        }
    }

    fun playPurchaseSuccess() {
        if (!isSoundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_CONFIRM, 200)
        } catch (e: Exception) {
            Log.w(TAG, "Play sound failed", e)
        }
    }

    fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }
}
