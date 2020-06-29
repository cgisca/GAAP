package io.cgisca.godotadmoblibrary.consent

import android.app.Activity
import android.util.Log
import com.google.ads.consent.ConsentForm
import com.google.ads.consent.ConsentFormListener
import com.google.ads.consent.ConsentInfoUpdateListener
import com.google.ads.consent.ConsentInformation
import com.google.ads.consent.ConsentStatus
import com.google.ads.consent.DebugGeography
import io.cgisca.godotadmoblibrary.AdState
import io.cgisca.godotadmoblibrary.BuildConfig
import java.net.MalformedURLException
import java.net.URL

class ConsentController(
  private val activity: Activity,
  private val adState: AdState,
  private val listener: ConsentListener
) {

  private lateinit var consentForm: ConsentForm
  private var testDeviceId: String = ""
  private var isEEAGeography: Boolean = false
  private var publisherIds: Array<String> = arrayOf()
  private var privacyPolicyUrl: String = ""

  fun init(publisherIds: Array<String>, privacyPolicyUrl: String) {
    this.publisherIds = publisherIds
    this.privacyPolicyUrl = privacyPolicyUrl
    consentForm = getConsentForm(getPrivacyPolicyUrl())
  }

  fun setTestDevice(testDeviceId: String, isEEAGeography: Boolean) {
    this.testDeviceId = testDeviceId
    this.isEEAGeography = isEEAGeography
  }

  fun getConsentInformation(isRevokingConsent: Boolean) {
    val consentInformation = ConsentInformation.getInstance(activity)
    if (testDeviceId.isNotEmpty()) {
      consentInformation.addTestDevice(testDeviceId)
      if (isEEAGeography) {
        consentInformation.debugGeography = DebugGeography.DEBUG_GEOGRAPHY_EEA
      } else {
        consentInformation.debugGeography = DebugGeography.DEBUG_GEOGRAPHY_NOT_EEA
      }
    }
    consentInformation.requestConsentInfoUpdate(publisherIds, object : ConsentInfoUpdateListener {
      override fun onConsentInfoUpdated(consentStatus: ConsentStatus) {
        if (isRevokingConsent) {
          consentForm.load()
        } else {
          if (consentInformation.isRequestLocationInEeaOrUnknown && consentStatus == ConsentStatus.UNKNOWN) {
            consentForm.load()
          } else {
            enableAdsAfterConsent(consentStatus)
          }
        }
        listener.onConsentInfoUpdated()
      }

      override fun onFailedToUpdateConsentInfo(errorDescription: String) {
        listener.onConsentInfoFailedToUpdate(errorDescription)
      }
    })
  }

  private fun enableAdsAfterConsent(consentStatus: ConsentStatus) {
    if (consentStatus == ConsentStatus.NON_PERSONALIZED) {
      adState.isNpaEnabled = true
    }
    adState.isAdEnabled = true
    listener.onConsentAdsAllowed()
  }

  private fun getPrivacyPolicyUrl(): URL? {
    var privacyUrl: URL? = null
    try {
      privacyUrl = URL(privacyPolicyUrl)
    } catch (e: MalformedURLException) {
      Log.d(BuildConfig.LIBRARY_NAME, "Privacy URL malformed: $privacyPolicyUrl, ${e.localizedMessage}")
      e.printStackTrace()
    }
    return privacyUrl
  }

  private fun getConsentForm(privacyUrl: URL?): ConsentForm {
    return ConsentForm.Builder(activity, privacyUrl)
      .withListener(object : ConsentFormListener() {
        override fun onConsentFormLoaded() {
          consentForm.show()
          listener.onConsentFormLoaded()
        }

        override fun onConsentFormOpened() {
          listener.onConsentFormOpened()
        }

        override fun onConsentFormClosed(consentStatus: ConsentStatus, userPrefersAdFree: Boolean) {
          if (userPrefersAdFree) {
            listener.onConsentUserPreferAdFree()
            adState.isAdEnabled = true
          } else if (consentStatus == ConsentStatus.NON_PERSONALIZED || consentStatus == ConsentStatus.PERSONALIZED) {
            enableAdsAfterConsent(consentStatus)
          } else {
            listener.onConsentEeaUserUnknown()
          }
        }

        override fun onConsentFormError(errorDescription: String) {
          listener.onConsentFormError(errorDescription)
        }
      })
      .withPersonalizedAdsOption()
      .withNonPersonalizedAdsOption()
      .withAdFreeOption()
      .build()
  }
}