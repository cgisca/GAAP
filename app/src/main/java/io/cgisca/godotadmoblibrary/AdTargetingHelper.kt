package io.cgisca.godotadmoblibrary

import android.os.Bundle
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.RequestConfiguration.MAX_AD_CONTENT_RATING_G
import com.google.android.gms.ads.RequestConfiguration.MAX_AD_CONTENT_RATING_MA
import com.google.android.gms.ads.RequestConfiguration.MAX_AD_CONTENT_RATING_PG
import com.google.android.gms.ads.RequestConfiguration.MAX_AD_CONTENT_RATING_T
import com.google.android.gms.ads.RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE
import com.google.android.gms.ads.RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE
import com.google.android.gms.ads.RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_UNSPECIFIED
import com.google.android.gms.ads.RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_FALSE
import com.google.android.gms.ads.RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE
import com.google.android.gms.ads.RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_UNSPECIFIED

class AdTargetingHelper(
  private val adState: AdState
) {

  fun getAdRequest(): AdRequest {
    val builder = AdRequest.Builder()
    if (adState.isNpaEnabled) {
      val extras = Bundle()
      extras.putString("npa", "1")
      builder.addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
    }
    return builder.build()
  }

  fun getRequestConfiguration(
    childDirectedTreatment: Boolean,
    underAgeOfConsent: Boolean,
    contentRating: String,
    testDeviceIds: Array<String>
  ): RequestConfiguration {
    val builder = MobileAds.getRequestConfiguration().toBuilder()
    builder.setTagForChildDirectedTreatment(getTagForChildDirectedTreatment(childDirectedTreatment))
    builder.setTagForUnderAgeOfConsent(getTagForUnderAgeOfConsent(underAgeOfConsent))
    builder.setMaxAdContentRating(getMaxAdContentRating(contentRating))
    if (testDeviceIds.isNotEmpty()) {
      builder.setTestDeviceIds(testDeviceIds.toList())
    }
    return builder.build()
  }

  private fun getTagForChildDirectedTreatment(type: Boolean): Int {
    return when (type) {
      false -> TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE
      true -> TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE
      else -> TAG_FOR_CHILD_DIRECTED_TREATMENT_UNSPECIFIED
    }
  }

  private fun getTagForUnderAgeOfConsent(type: Boolean): Int {
    return when (type) {
      false -> TAG_FOR_UNDER_AGE_OF_CONSENT_FALSE
      true -> TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE
      else -> TAG_FOR_UNDER_AGE_OF_CONSENT_UNSPECIFIED
    }
  }

  private fun getMaxAdContentRating(type: String): String? {
    return when (type) {
      "PG" -> MAX_AD_CONTENT_RATING_PG
      "T" -> MAX_AD_CONTENT_RATING_T
      "MA" -> MAX_AD_CONTENT_RATING_MA
      else -> MAX_AD_CONTENT_RATING_G
    }
  }
}