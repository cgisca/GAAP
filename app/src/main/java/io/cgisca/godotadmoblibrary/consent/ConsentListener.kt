package io.cgisca.godotadmoblibrary.consent

interface ConsentListener {
  fun onConsentAdsAllowed()
  fun onConsentFormLoaded()
  fun onConsentFormOpened()
  fun onConsentUserPreferAdFree()
  fun onConsentEeaUserUnknown()
  fun onConsentFormError(errorDescription: String)
  fun onConsentInfoUpdated()
  fun onConsentInfoFailedToUpdate(errorDescription: String)
}