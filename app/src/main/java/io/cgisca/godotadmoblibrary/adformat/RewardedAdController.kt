package io.cgisca.godotadmoblibrary.adformat

import android.app.Activity
import android.util.Log
import androidx.annotation.NonNull
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import io.cgisca.godotadmoblibrary.AdState
import io.cgisca.godotadmoblibrary.BuildConfig

class RewardedAdController(
  private val activity: Activity,
  private val adRequest: AdRequest,
  private val adState: AdState,
  private val listener: RewardedAdListener
) {

  private val rewardedAds = mutableMapOf<String, RewardedAd>()

  private fun createAndLoadRewardedAd(adUnitId: String): RewardedAd {
    val rewardedAd = RewardedAd(activity, adUnitId)
    val adLoadCallback = object : RewardedAdLoadCallback() {

      override fun onRewardedAdLoaded() {
        Log.d(BuildConfig.LIBRARY_NAME, "Reward with unitId $adUnitId loaded")
        listener.onRewardedAdLoaded(adUnitId)
      }

      override fun onRewardedAdFailedToLoad(errorCode: Int) {
        Log.d(BuildConfig.LIBRARY_NAME, "Reward with unitId $adUnitId failed to load. Error code: $errorCode")
        listener.onRewardedAdFailedToLoad(adUnitId, errorCode)
      }
    }
    rewardedAd.loadAd(adRequest, adLoadCallback)
    return rewardedAd
  }

  fun loadRewardedAds(adUnitIds: Array<String>) {
    adUnitIds.forEach {
      val rewardedAd = createAndLoadRewardedAd(it)
      rewardedAds[it] = rewardedAd
    }
    Log.d(BuildConfig.LIBRARY_NAME, "Loaded rewarded ads with unitIds: ${rewardedAds.keys}")
  }

  fun showRewardedAd(adUnitId: String) {
    val rewardedAd = rewardedAds[adUnitId]
    if (rewardedAd != null) {
      if (rewardedAd.isLoaded && adState.isAdEnabled) {
        val adCallback = object : RewardedAdCallback() {
          override fun onRewardedAdOpened() {
            Log.d(BuildConfig.LIBRARY_NAME, "Reward with unitId $adUnitId opened")
            listener.onRewardedAdOpened(adUnitId)
          }

          override fun onRewardedAdClosed() {
            Log.d(BuildConfig.LIBRARY_NAME, "Reward with unitId $adUnitId closed")
            listener.onRewardedAdClosed(adUnitId)
          }

          override fun onUserEarnedReward(@NonNull reward: RewardItem) {
            Log.d(
              BuildConfig.LIBRARY_NAME,
              "User earned reward (Type: ${reward.type}, Amount: ${reward.amount}) for ad with unitId $adUnitId"
            )
            listener.onUserEarnedReward(adUnitId, reward)
          }

          override fun onRewardedAdFailedToShow(errorCode: Int) {
            Log.d(BuildConfig.LIBRARY_NAME, "Reward with unitId $adUnitId failed to show. Error code: $errorCode")
            listener.onRewardedAdFailedToShow(adUnitId, errorCode)
          }
        }
        rewardedAd.show(activity, adCallback)
      } else {
        Log.d(BuildConfig.LIBRARY_NAME, "Reward with unitId $adUnitId not loaded or ad not enabled")
        listener.onRewardedAdNotLoaded(adUnitId)
      }
    } else {
      Log.d(BuildConfig.LIBRARY_NAME, "Reward with unitId $adUnitId not loaded")
      listener.onRewardedAdNotLoaded(adUnitId)
    }
  }
}