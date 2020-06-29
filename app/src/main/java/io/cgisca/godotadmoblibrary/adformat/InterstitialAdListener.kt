package io.cgisca.godotadmoblibrary.adformat

interface InterstitialAdListener {
  fun onInterstitialAdLoaded()
  fun onInterstitialAdFailedToLoad(errorCode: Int)
  fun onInterstitialAdOpened()
  fun onInterstitialAdClicked()
  fun onInterstitialAdLeftApp()
  fun onInterstitialAdClosed()
  fun onInterstitialNotLoadedYet()
}