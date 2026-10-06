package com.example.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

private const val TAG = "AdManager"

class AdManager(private val context: Context) {

    companion object {
        // Official Google AdMob sample test ad unit IDs
        const val BANNER_TEST_ID = "ca-app-pub-3940256099942544/6300978111"
        const val INTERSTITIAL_TEST_ID = "ca-app-pub-3940256099942544/1033173712"
        const val REWARDED_TEST_ID = "ca-app-pub-3940256099942544/5224354917"
    }

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var isInitialized = false
    private var isInterstitialLoading = false
    private var isRewardedLoading = false

    fun initialize() {
        if (isInitialized) return
        try {
            // Configure MobileAds
            val requestConfig = RequestConfiguration.Builder()
                .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR))
                .build()
            MobileAds.setRequestConfiguration(requestConfig)

            MobileAds.initialize(context) { status ->
                Log.d(TAG, "AdMob MobileAds initialized")
                isInitialized = true
                // Preload interstitial only; rewarded ad is loaded lazily on demand
                loadInterstitialAd()
            }
        } catch (t: Throwable) {
            Log.w(TAG, "AdMob initialization handled: ${t.message}")
        }
    }

    fun loadInterstitialAd() {
        if (isInterstitialLoading || interstitialAd != null) return
        isInterstitialLoading = true
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            INTERSTITIAL_TEST_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isInterstitialLoading = false
                    Log.d(TAG, "Interstitial ad loaded")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.w(TAG, "Interstitial ad failed: ${loadAdError.message}")
                    interstitialAd = null
                    isInterstitialLoading = false
                }
            }
        )
    }

    fun showInterstitial(activity: Activity, isPremium: Boolean, onDismiss: () -> Unit) {
        if (isPremium) {
            onDismiss()
            return
        }

        if (!isInitialized) {
            initialize()
            onDismiss()
            return
        }

        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitialAd()
                    onDismiss()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.w(TAG, "Failed to show interstitial: ${adError.message}")
                    interstitialAd = null
                    onDismiss()
                }
            }
            ad.show(activity)
        } else {
            loadInterstitialAd()
            onDismiss()
        }
    }

    fun loadRewardedAd() {
        if (isRewardedLoading || rewardedAd != null) return
        isRewardedLoading = true
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            REWARDED_TEST_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isRewardedLoading = false
                    Log.d(TAG, "Rewarded ad loaded")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.w(TAG, "Rewarded ad failed: ${loadAdError.message}")
                    rewardedAd = null
                    isRewardedLoading = false
                }
            }
        )
    }

    fun showRewarded(
        activity: Activity,
        isPremium: Boolean,
        onUserEarnedReward: () -> Unit,
        onDismiss: () -> Unit
    ) {
        if (isPremium) {
            // Premium users get the reward instantly without watching an ad
            onUserEarnedReward()
            onDismiss()
            return
        }

        val ad = rewardedAd
        if (ad != null) {
            var earned = false
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    loadRewardedAd()
                    if (earned) onUserEarnedReward()
                    onDismiss()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.w(TAG, "Failed to show rewarded: ${adError.message}")
                    rewardedAd = null
                    onDismiss()
                }
            }
            ad.show(activity) { rewardItem ->
                earned = true
                Log.d(TAG, "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
            }
        } else {
            // Ad was not ready yet, grant reward or load
            onUserEarnedReward()
            loadRewardedAd()
            onDismiss()
        }
    }
}
