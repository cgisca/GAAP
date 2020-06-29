package io.cgisca.godotadmoblibrary.adformat

interface BannerAdListener {
  fun onBannerAdLoaded()
  fun onBannerAdFailedToLoad(errorCode: Int)
  fun onBannerAdOpened()
  fun onBannerAdClicked()
  fun onBannerAdLeftApp()
  fun onBannerAdClosed()
}