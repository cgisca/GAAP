package io.cgisca.godotadmoblibrary.adformat

import com.google.android.gms.ads.rewarded.RewardItem

interface RewardedAdListener {
  fun onRewardedAdLoaded(adUnitId: String)
  fun onRewardedAdFailedToLoad(adUnitId: String, errorCode: Int)
  fun onRewardedAdNotLoaded(adUnitId: String)
  fun onRewardedAdOpened(adUnitId: String)
  fun onRewardedAdClosed(adUnitId: String)
  fun onUserEarnedReward(adUnitId: String, reward: RewardItem)
  fun onRewardedAdFailedToShow(adUnitId: String, errorCode: Int)
}