package io.cgisca.godotadmoblibrary

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardItem
import io.cgisca.godotadmoblibrary.adformat.BannerAdController
import io.cgisca.godotadmoblibrary.adformat.BannerAdListener
import io.cgisca.godotadmoblibrary.adformat.InterstitialAdController
import io.cgisca.godotadmoblibrary.adformat.InterstitialAdListener
import io.cgisca.godotadmoblibrary.adformat.RewardedAdController
import io.cgisca.godotadmoblibrary.adformat.RewardedAdListener
import io.cgisca.godotadmoblibrary.consent.ConsentController
import io.cgisca.godotadmoblibrary.consent.ConsentListener
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo

class AdmobGodot(godot: Godot) : GodotPlugin(godot), BannerAdListener, InterstitialAdListener, RewardedAdListener, ConsentListener {

  private val context: Context = godot.applicationContext
  private lateinit var layout: FrameLayout

  private val adState = AdState()

  private val adTargetingHelper = AdTargetingHelper(adState)
  private var bannerAdController: BannerAdController? = null
  private val interstitialAdController = InterstitialAdController(godot as Activity, adTargetingHelper.getAdRequest(), adState, this)
  private val rewardedAdController = RewardedAdController(godot as Activity, adTargetingHelper.getAdRequest(), adState, this)
  private val consentController = ConsentController(godot as Activity, adState, this)

  companion object {
    val SIGNAL_ADS_INITIALIZED = SignalInfo("on_ads_initialized")
    val SIGNAL_BANNER_AD_LOADED = SignalInfo("on_banner_ad_loaded")
    val SIGNAL_BANNER_AD_FAILED_TO_LOAD = SignalInfo("on_banner_ad_failed_to_load", Int::class.javaObjectType)
    val SIGNAL_BANNER_AD_OPENED = SignalInfo("on_banner_ad_opened")
    val SIGNAL_BANNER_AD_CLICKED = SignalInfo("on_banner_ad_clicked")
    val SIGNAL_BANNER_AD_LEFT_APP = SignalInfo("on_banner_ad_left_app")
    val SIGNAL_BANNER_AD_CLOSED = SignalInfo("on_banner_ad_closed")
    val SIGNAL_INTERSTITIAL_AD_LOADED = SignalInfo("on_interstitial_ad_loaded")
    val SIGNAL_INTERSTITIAL_AD_FAILED_TO_LOAD = SignalInfo("on_interstitial_ad_failed_to_load", Int::class.javaObjectType)
    val SIGNAL_INTERSTITIAL_AD_OPENED = SignalInfo("on_interstitial_ad_opened")
    val SIGNAL_INTERSTITIAL_AD_CLICKED = SignalInfo("on_interstitial_ad_clicked")
    val SIGNAL_INTERSTITIAL_AD_LEFT_APP = SignalInfo("on_interstitial_ad_left_app")
    val SIGNAL_INTERSTITIAL_AD_CLOSED = SignalInfo("on_interstitial_ad_closed")
    val SIGNAL_INTERSTITIAL_AD_NOT_LOADED_YET = SignalInfo("on_interstitial_ad_not_loaded_yet")
    val SIGNAL_REWARDED_AD_LOADED = SignalInfo("on_rewarded_ad_loaded", String::class.java)
    val SIGNAL_REWARDED_AD_FAILED_TO_LOAD = SignalInfo("on_rewarded_ad_failed_to_load", String::class.java, Int::class.javaObjectType)
    val SIGNAL_REWARDED_AD_NOT_LOADED = SignalInfo("on_rewarded_ad_not_loaded", String::class.java)
    val SIGNAL_REWARDED_AD_OPENED = SignalInfo("on_rewarded_ad_opened", String::class.java)
    val SIGNAL_REWARDED_AD_CLOSED = SignalInfo("on_rewarded_ad_closed", String::class.java)
    val SIGNAL_REWARDED_AD_EARNED = SignalInfo("on_rewarded_ad_earned", String::class.java, String::class.java, Int::class.javaObjectType)
    val SIGNAL_REWARDED_AD_FAILED_TO_SHOW = SignalInfo("on_rewarded_ad_failed_to_show", String::class.java, Int::class.javaObjectType)
    val SIGNAL_CONSENT_ADS_ALLOWED = SignalInfo("on_consent_ads_allowed")
    val SIGNAL_CONSENT_FORM_LOADED = SignalInfo("on_consent_form_loaded")
    val SIGNAL_CONSENT_FORM_OPENED = SignalInfo("on_consent_form_opened")
    val SIGNAL_CONSENT_USER_PREFER_AD_FREE = SignalInfo("on_consent_user_prefer_ad_free")
    val SIGNAL_CONSENT_EEA_USER_UNKOWN = SignalInfo("on_consent_eea_user_unkown")
    val SIGNAL_CONSENT_FORM_ERROR = SignalInfo("on_consent_form_error", String::class.java)
    val SIGNAL_CONSENT_INFO_UPDATED = SignalInfo("on_consent_info_updated")
    val SIGNAL_CONSENT_INFO_FAILED_TO_UPDATE = SignalInfo("on_consent_info_failed_to_update", String::class.java)
  }

  override fun getPluginName(): String {
    return BuildConfig.LIBRARY_NAME
  }

  override fun getPluginMethods(): MutableList<String> {
    return mutableListOf(
      "initTargetedWithConsent",
      "initTargetedWithoutConsent",
      "initNonTargetedWithoutConsent",
      "loadBannerAd",
      "showBannerAd",
      "hideBannerAd",
      "resizeBannerAd",
      "loadInterstitialAd",
      "showInterstitialAd",
      "loadRewardedAds",
      "loadRewardedAd",
      "showRewardedAd",
      "setAdVolume",
      "setTestDeviceForConsentInfo",
      "showConsentInfo",
      "revokeConsentInfo"
    )
  }

  override fun getPluginSignals(): MutableSet<SignalInfo> {
    return mutableSetOf(
      SIGNAL_ADS_INITIALIZED,
      SIGNAL_BANNER_AD_LOADED,
      SIGNAL_BANNER_AD_FAILED_TO_LOAD,
      SIGNAL_BANNER_AD_OPENED,
      SIGNAL_BANNER_AD_CLICKED,
      SIGNAL_BANNER_AD_LEFT_APP,
      SIGNAL_BANNER_AD_CLOSED,
      SIGNAL_INTERSTITIAL_AD_LOADED,
      SIGNAL_INTERSTITIAL_AD_FAILED_TO_LOAD,
      SIGNAL_INTERSTITIAL_AD_OPENED,
      SIGNAL_INTERSTITIAL_AD_CLICKED,
      SIGNAL_INTERSTITIAL_AD_LEFT_APP,
      SIGNAL_INTERSTITIAL_AD_CLOSED,
      SIGNAL_INTERSTITIAL_AD_NOT_LOADED_YET,
      SIGNAL_REWARDED_AD_LOADED,
      SIGNAL_REWARDED_AD_FAILED_TO_LOAD,
      SIGNAL_REWARDED_AD_NOT_LOADED,
      SIGNAL_REWARDED_AD_OPENED,
      SIGNAL_REWARDED_AD_CLOSED,
      SIGNAL_REWARDED_AD_EARNED,
      SIGNAL_REWARDED_AD_FAILED_TO_SHOW,
      SIGNAL_CONSENT_ADS_ALLOWED,
      SIGNAL_CONSENT_FORM_LOADED,
      SIGNAL_CONSENT_FORM_OPENED,
      SIGNAL_CONSENT_USER_PREFER_AD_FREE,
      SIGNAL_CONSENT_EEA_USER_UNKOWN,
      SIGNAL_CONSENT_FORM_ERROR,
      SIGNAL_CONSENT_INFO_UPDATED,
      SIGNAL_CONSENT_INFO_FAILED_TO_UPDATE
    )
  }

  override fun onMainCreate(activity: Activity): View {
    layout = FrameLayout(activity);
    return layout;
  }

  fun initTargetedWithConsent(
    isForChildDirectedTreatment: Boolean,
    isUnderAgeOfConsent: Boolean,
    maxAdContentRating: String,
    testDeviceIds: Array<String>,
    publisherIds: Array<String>,
    privacyPolicy: String
  ) {
    MobileAds.initialize(context) {
      emitSignal(SIGNAL_ADS_INITIALIZED.name)

      MobileAds.setRequestConfiguration(
        adTargetingHelper.getRequestConfiguration(
          isForChildDirectedTreatment,
          isUnderAgeOfConsent,
          maxAdContentRating,
          testDeviceIds
        )
      )

      consentController.init(publisherIds, privacyPolicy)
    }
  }

  fun initTargetedWithoutConsent(
    isForChildDirectedTreatment: Boolean,
    isUnderAgeOfConsent: Boolean,
    maxAdContentRating: String,
    testDeviceIds: Array<String>
  ) {
    MobileAds.initialize(context) {
      emitSignal(SIGNAL_ADS_INITIALIZED.name)

      MobileAds.setRequestConfiguration(
        adTargetingHelper.getRequestConfiguration(
          isForChildDirectedTreatment,
          isUnderAgeOfConsent,
          maxAdContentRating,
          testDeviceIds
        )
      )

      adState.isAdEnabled = true
    }
  }

  fun initNonTargetedWithoutConsent() {
    MobileAds.initialize(context) {
      emitSignal(SIGNAL_ADS_INITIALIZED.name)

      MobileAds.setRequestConfiguration(
        adTargetingHelper.getRequestConfiguration(
          childDirectedTreatment = false,
          underAgeOfConsent = false,
          contentRating = "G",
          testDeviceIds = emptyArray()
        )
      )

      adState.isAdEnabled = true
    }
  }

  fun loadBannerAd(unitId: String, showOnTop: Boolean) {
    bannerAdController = BannerAdController(
      unitId,
      adTargetingHelper.getAdRequest(),
      showOnTop,
      godot as Activity,
      layout,
      adState,
      this
    )

    runOnUiThread {
      bannerAdController?.init()
    }
  }

  fun showBannerAd() {
    runOnUiThread {
      bannerAdController?.show()
    }
  }

  fun hideBannerAd() {
    runOnUiThread {
      bannerAdController?.hide()
    }
  }

  fun resizeBannerAd() {
    runOnUiThread {
      bannerAdController?.resize()
    }
  }

  fun loadInterstitialAd(adUnitId: String) {
    runOnUiThread {
      interstitialAdController.load(adUnitId)
    }
  }

  fun showInterstitialAd() {
    runOnUiThread {
      interstitialAdController.show()
    }
  }

  fun loadRewardedAds(unitIds: Array<String>) {
    runOnUiThread {
      rewardedAdController.loadRewardedAds(unitIds)
    }
  }

  fun loadRewardedAd(unitId: String) {
    runOnUiThread {
      rewardedAdController.loadRewardedAds(arrayOf(unitId))
    }
  }

  fun showRewardedAd(unitId: String) {
    runOnUiThread {
      rewardedAdController.showRewardedAd(unitId)
    }
  }

  fun setAdVolume(volume: Float) {
    MobileAds.setAppVolume(volume)
  }

  fun setTestDeviceForConsentInfo(testDevice: String, isEeaGeography: Boolean) {
    consentController.setTestDevice(testDevice, isEeaGeography)
  }

  fun showConsentInfo() {
    runOnUiThread {
      consentController.getConsentInformation(false)
    }
  }

  fun revokeConsentInfo() {
    runOnUiThread {
      consentController.getConsentInformation(true)
    }
  }

  override fun onBannerAdLoaded() {
    emitSignal(SIGNAL_BANNER_AD_LOADED.name)
  }

  override fun onBannerAdFailedToLoad(errorCode: Int) {
    emitSignal(SIGNAL_BANNER_AD_FAILED_TO_LOAD.name, errorCode)
  }

  override fun onBannerAdOpened() {
    emitSignal(SIGNAL_BANNER_AD_OPENED.name)
  }

  override fun onBannerAdClicked() {
    emitSignal(SIGNAL_BANNER_AD_CLICKED.name)
  }

  override fun onBannerAdLeftApp() {
    emitSignal(SIGNAL_BANNER_AD_LEFT_APP.name)
  }

  override fun onBannerAdClosed() {
    emitSignal(SIGNAL_BANNER_AD_CLOSED.name)
  }

  override fun onInterstitialAdLoaded() {
    emitSignal(SIGNAL_INTERSTITIAL_AD_LOADED.name)
  }

  override fun onInterstitialAdFailedToLoad(errorCode: Int) {
    emitSignal(SIGNAL_INTERSTITIAL_AD_FAILED_TO_LOAD.name, errorCode)
  }

  override fun onInterstitialAdOpened() {
    emitSignal(SIGNAL_INTERSTITIAL_AD_OPENED.name)
  }

  override fun onInterstitialAdClicked() {
    emitSignal(SIGNAL_INTERSTITIAL_AD_CLICKED.name)
  }

  override fun onInterstitialAdLeftApp() {
    emitSignal(SIGNAL_INTERSTITIAL_AD_LEFT_APP.name)
  }

  override fun onInterstitialAdClosed() {
    emitSignal(SIGNAL_INTERSTITIAL_AD_CLOSED.name)
  }

  override fun onInterstitialNotLoadedYet() {
    emitSignal(SIGNAL_INTERSTITIAL_AD_NOT_LOADED_YET.name)
  }

  override fun onRewardedAdLoaded(adUnitId: String) {
    emitSignal(SIGNAL_REWARDED_AD_LOADED.name, adUnitId)
  }

  override fun onRewardedAdFailedToLoad(adUnitId: String, errorCode: Int) {
    emitSignal(SIGNAL_REWARDED_AD_FAILED_TO_LOAD.name, adUnitId, errorCode)
  }

  override fun onRewardedAdNotLoaded(adUnitId: String) {
    emitSignal(SIGNAL_REWARDED_AD_NOT_LOADED.name, adUnitId)
  }

  override fun onRewardedAdOpened(adUnitId: String) {
    emitSignal(SIGNAL_REWARDED_AD_OPENED.name, adUnitId)
  }

  override fun onRewardedAdClosed(adUnitId: String) {
    emitSignal(SIGNAL_REWARDED_AD_CLOSED.name, adUnitId)
  }

  override fun onUserEarnedReward(adUnitId: String, reward: RewardItem) {
    emitSignal(SIGNAL_REWARDED_AD_EARNED.name, adUnitId, reward.type, reward.amount)
  }

  override fun onRewardedAdFailedToShow(adUnitId: String, errorCode: Int) {
    emitSignal(SIGNAL_REWARDED_AD_FAILED_TO_SHOW.name, adUnitId, errorCode)
  }

  override fun onConsentAdsAllowed() {
    emitSignal(SIGNAL_CONSENT_ADS_ALLOWED.name)
  }

  override fun onConsentFormLoaded() {
    emitSignal(SIGNAL_CONSENT_FORM_LOADED.name)
  }

  override fun onConsentFormOpened() {
    emitSignal(SIGNAL_CONSENT_FORM_OPENED.name)
  }

  override fun onConsentUserPreferAdFree() {
    emitSignal(SIGNAL_CONSENT_USER_PREFER_AD_FREE.name)
  }

  override fun onConsentEeaUserUnknown() {
    emitSignal(SIGNAL_CONSENT_EEA_USER_UNKOWN.name)
  }

  override fun onConsentFormError(errorDescription: String) {
    emitSignal(SIGNAL_CONSENT_FORM_ERROR.name, errorDescription)
  }

  override fun onConsentInfoUpdated() {
    emitSignal(SIGNAL_CONSENT_INFO_UPDATED.name)
  }

  override fun onConsentInfoFailedToUpdate(errorDescription: String) {
    emitSignal(SIGNAL_CONSENT_INFO_FAILED_TO_UPDATE.name, errorDescription)
  }
}