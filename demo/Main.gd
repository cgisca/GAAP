extends Control

const REWARDED_AD_ID := "ca-app-pub-3940256099942544/5224354917"


func _ready():
	get_tree().get_root().connect("size_changed", self, "_orientation_changed")
	$AdMobAndroid.load_interstitial()


func _orientation_changed() :
	$AdMobAndroid.resize_banner()


func _on_ShowBannerAdButton_pressed():
	$AdMobAndroid.load_banner()


func _on_ShowInterstitialAdButton_pressed():
	$AdMobAndroid.show_interstitial()


func _on_ShowRewardedAdButton_pressed():
	var list_of_rewarded_ad_ids := [REWARDED_AD_ID]
	$AdMobAndroid.load_rewarded_ads(list_of_rewarded_ad_ids)


func _on_ShowConsentButton_pressed():
	if $AdMobAndroid.init_type != $AdMobAndroid.INIT_TYPE.TARGETED_WITH_CONSENT:
		print("Set init type for $AdMobAndroid to be TARGETED_WITH_CONSENT")
		return
	$AdMobAndroid.show_consent_info()


func _on_AdMobAndroid_on_ads_initialized():
	$Blocker.visible = false
	$AdMobAndroid.set_ads_volume(0.5)
	print("Ads initialized. At this moment ads can be shown. Also can be set other settings like ads volume")


func _on_AdMobAndroid_on_rewarded_ad_loaded(unit_id):
	$AdMobAndroid.show_rewarded_ad(unit_id)


func _on_AdMobAndroid_on_consent_ads_allowed():
	pass # Replace with function body.


func _on_AdMobAndroid_on_consent_eea_user_unkown():
	pass # Replace with function body.


func _on_AdMobAndroid_on_consent_form_error(error):
	print(error)


func _on_AdMobAndroid_on_consent_form_loaded():
	pass # Replace with function body.


func _on_AdMobAndroid_on_consent_form_opened():
	pass # Replace with function body.


func _on_AdMobAndroid_on_consent_info_failed_to_update(error):
	print(error)
