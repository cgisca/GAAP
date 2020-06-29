package io.cgisca.godotadmoblibrary.adformat

import android.app.Activity
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import io.cgisca.godotadmoblibrary.AdState
import io.cgisca.godotadmoblibrary.BuildConfig

class BannerAdController(
  private val unitId: String,
  private val adRequest: AdRequest,
  private val showOnTop: Boolean,
  private val activity: Activity,
  private val layout: FrameLayout,
  private val adState: AdState,
  private val listener: BannerAdListener
) {

  private var adView = AdView(activity)

  fun init() {
    if (adState.isAdEnabled) {
      if (layout.childCount > 0) {
        layout.removeAllViews()
      }

      adView.adSize = AdSize.SMART_BANNER
      adView.adUnitId = unitId
      adView.setBackgroundColor(Color.TRANSPARENT)

      layout.addView(adView, getLayoutParams())

      adView.loadAd(adRequest)

      adView.adListener = bannerAdListener
    }
  }

  fun show() {
    if (adState.isAdEnabled && adView.visibility != View.VISIBLE) {
      adView.visibility = View.VISIBLE
      adView.resume()
    }
  }

  fun hide() {
    if (adView.visibility != View.GONE) {
      adView.visibility = View.GONE
      adView.pause()
    }
  }

  fun resize() {
    if (adState.isAdEnabled) {

      if (layout.childCount > 0) {
        layout.removeAllViews()
      }

      adView = AdView(activity)
      adView.adUnitId = unitId
      adView.setBackgroundColor(Color.TRANSPARENT)
      adView.adSize = AdSize.SMART_BANNER
      layout.addView(adView, getLayoutParams())
      adView.adListener = bannerAdListener
      adView.loadAd(adRequest)
    }
  }

  private fun getLayoutParams(): FrameLayout.LayoutParams {
    val adParams = FrameLayout.LayoutParams(
      FrameLayout.LayoutParams.MATCH_PARENT,
      FrameLayout.LayoutParams.WRAP_CONTENT
    )

    adParams.gravity = when (showOnTop) {
      true -> Gravity.TOP
      false -> Gravity.BOTTOM
    }

    return adParams
  }

  private val bannerAdListener = object : AdListener() {
    override fun onAdLoaded() {
      Log.d(BuildConfig.LIBRARY_NAME, "Banner ad loaded")
      listener.onBannerAdLoaded()
    }

    override fun onAdFailedToLoad(errorCode: Int) {
      Log.d(BuildConfig.LIBRARY_NAME, "Banner ad failed to load. Error code: $errorCode")
      listener.onBannerAdFailedToLoad(errorCode)
    }

    override fun onAdOpened() {
      Log.d(BuildConfig.LIBRARY_NAME, "Banner ad opened")
      listener.onBannerAdOpened()
    }

    override fun onAdClicked() {
      Log.d(BuildConfig.LIBRARY_NAME, "Banner ad clicked")
      listener.onBannerAdClicked()
    }

    override fun onAdLeftApplication() {
      Log.d(BuildConfig.LIBRARY_NAME, "Banner ad left application")
      listener.onBannerAdLeftApp()
    }

    override fun onAdClosed() {
      Log.d(BuildConfig.LIBRARY_NAME, "Banner ad closed")
      listener.onBannerAdClosed()
    }
  }
}