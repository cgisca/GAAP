package io.cgisca.godotadmoblibrary.adformat

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import io.cgisca.godotadmoblibrary.AdState
import io.cgisca.godotadmoblibrary.BuildConfig

class InterstitialAdController(
  private val activity: Activity,
  private val adRequest: AdRequest,
  private val adState: AdState,
  private val listener: InterstitialAdListener
) {

  private val interstitialAd = InterstitialAd(activity)

  init {
    activity.runOnUiThread {
      interstitialAd.adListener = object : AdListener() {
        override fun onAdLoaded() {
          Log.d(BuildConfig.LIBRARY_NAME, "Interstitial ad loaded")
          listener.onInterstitialAdLoaded()
        }

        override fun onAdFailedToLoad(errorCode: Int) {
          Log.d(BuildConfig.LIBRARY_NAME, "Interstitial ad failed to load. Error code: $errorCode")
          listener.onInterstitialAdFailedToLoad(errorCode)
        }

        override fun onAdOpened() {
          Log.d(BuildConfig.LIBRARY_NAME, "Interstitial ad opened")
          listener.onInterstitialAdOpened()
        }

        override fun onAdClicked() {
          Log.d(BuildConfig.LIBRARY_NAME, "Interstitial ad clicked")
          listener.onInterstitialAdClicked()
        }

        override fun onAdLeftApplication() {
          Log.d(BuildConfig.LIBRARY_NAME, "Interstitial ad left application")
          listener.onInterstitialAdLeftApp()
        }

        override fun onAdClosed() {
          Log.d(BuildConfig.LIBRARY_NAME, "Interstitial ad closed")
          listener.onInterstitialAdClosed()
          interstitialAd.loadAd(adRequest)
        }
      }
    }
  }

  fun load(unitId: String) {
    if (interstitialAd.adUnitId == null) {
      interstitialAd.adUnitId = unitId
    }
    interstitialAd.loadAd(adRequest)
  }

  fun show() {
    if (interstitialAd.isLoaded && adState.isAdEnabled) {
      interstitialAd.show()
    } else {
      Log.d(BuildConfig.LIBRARY_NAME, "Interstitial ad not loaded yet or ad not enabled")
      listener.onInterstitialNotLoadedYet()
    }
  }
}