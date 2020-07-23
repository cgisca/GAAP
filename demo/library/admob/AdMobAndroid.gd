extends Node
class_name AdMobAndroid, "res://library/admob/icon.png"

signal on_ads_initialized
signal on_banner_ad_loaded
signal on_banner_ad_failed_to_load(error_code)
signal on_banner_ad_opened
signal on_banner_ad_clicked
signal on_banner_ad_left_app
signal on_banner_ad_closed
signal on_interstitial_ad_loaded
signal on_interstitial_ad_failed_to_load(errro_code)
signal on_interstitial_ad_opened
signal on_interstitial_ad_clicked
signal on_interstitial_ad_left_app
signal on_interstitial_ad_closed
signal on_interstitial_ad_not_loaded_yet
signal on_rewarded_ad_loaded(unit_id)
signal on_rewarded_ad_failed_to_load(unit_id, error_code)
signal on_rewarded_ad_not_loaded(unit_id)
signal on_rewarded_ad_opened(unit_id)
signal on_rewarded_ad_closed(unit_id)
signal on_rewarded_ad_earned(unit_id, type, amount)
signal on_rewarded_ad_failed_to_show(unit_id, error_code)
signal on_consent_ads_allowed
signal on_consent_form_loaded
signal on_consent_form_opened
signal on_consent_user_prefer_ad_free
signal on_consent_eea_user_unkown
signal on_consent_form_error(error)
signal on_consent_info_updated
signal on_consent_info_failed_to_update(error)

enum INIT_TYPE { TARGETED_WITH_CONSENT, TARGETED_WITHOUT_CONSENT, NON_TARGETED_WITHOUT_CONSENT }

export (INIT_TYPE) var init_type
export (String, "G", "PG", "T", "MA") var max_ad_content_rate
export (bool) var is_for_child_directed_treatment
export (bool) var is_under_age_of_consent
export (String) var privacy_policy_url
export (Array, String) var publisher_ids = []
export (Array, String) var test_devices_id_request_configuration = []
export (String) var test_device_id_consent
export (bool) var test_consent_is_eea_geography
export (bool) var is_banner_ad_on_top
export (String) var banner_ad_unit_id
export (String) var interstitial_ad_unit_id
export (float) var ad_volume := 0.5

var _admob_singleton


func _enter_tree() -> void:
	if Engine.has_singleton("GodotAndroidAdMob"):
		_admob_singleton = Engine.get_singleton("GodotAndroidAdMob")
		
		init()
		_admob_singleton.setTestDeviceForConsentInfo(test_device_id_consent, test_consent_is_eea_geography)
		
		_admob_singleton.connect("on_ads_initialized", self, "_on_ads_initialized")
		_admob_singleton.connect("on_banner_ad_loaded", self, "_on_banner_ad_loaded")
		_admob_singleton.connect("on_banner_ad_failed_to_load", self, "_on_banner_ad_failed_to_load")
		_admob_singleton.connect("on_banner_ad_opened", self, "_on_banner_ad_opened")
		_admob_singleton.connect("on_banner_ad_clicked", self, "_on_banner_ad_clicked")
		_admob_singleton.connect("on_banner_ad_left_app", self, "_on_banner_ad_left_app")
		_admob_singleton.connect("on_banner_ad_closed", self, "_on_banner_ad_closed")
		_admob_singleton.connect("on_interstitial_ad_loaded", self, "_on_interstitial_ad_loaded")
		_admob_singleton.connect("on_interstitial_ad_failed_to_load", self, "_on_interstitial_ad_failed_to_load")
		_admob_singleton.connect("on_interstitial_ad_opened", self, "_on_interstitial_ad_opened")
		_admob_singleton.connect("on_interstitial_ad_clicked", self, "_on_interstitial_ad_clicked")
		_admob_singleton.connect("on_interstitial_ad_left_app", self, "_on_interstitial_ad_left_app")
		_admob_singleton.connect("on_interstitial_ad_closed", self, "_on_interstitial_ad_closed")
		_admob_singleton.connect("on_interstitial_ad_not_loaded_yet", self, "_on_interstitial_ad_not_loaded_yet")
		_admob_singleton.connect("on_rewarded_ad_loaded", self, "_on_rewarded_ad_loaded")
		_admob_singleton.connect("on_rewarded_ad_failed_to_load", self, "_on_rewarded_ad_failed_to_load")
		_admob_singleton.connect("on_rewarded_ad_not_loaded", self, "_on_rewarded_ad_not_loaded")
		_admob_singleton.connect("on_rewarded_ad_opened", self, "_on_rewarded_ad_opened")
		_admob_singleton.connect("on_rewarded_ad_closed", self, "_on_rewarded_ad_closed")
		_admob_singleton.connect("on_rewarded_ad_earned", self, "_on_rewarded_ad_earned")
		_admob_singleton.connect("on_rewarded_ad_failed_to_show", self, "_on_rewarded_ad_failed_to_show")
		_admob_singleton.connect("on_consent_ads_allowed", self, "_on_consent_ads_allowed")
		_admob_singleton.connect("on_consent_form_loaded", self, "_on_consent_form_loaded")
		_admob_singleton.connect("on_consent_form_opened", self, "_on_consent_form_opened")
		_admob_singleton.connect("on_consent_user_prefer_ad_free", self, "_on_consent_user_prefer_ad_free")
		_admob_singleton.connect("on_consent_eea_user_unkown", self, "_on_consent_eea_user_unkown")
		_admob_singleton.connect("on_consent_form_error", self, "_on_consent_form_error")
		_admob_singleton.connect("on_consent_info_updated", self, "_on_consent_info_updated")
		_admob_singleton.connect("on_consent_info_failed_to_update", self, "_on_consent_info_failed_to_update")


func init() -> void:
	match init_type:
		INIT_TYPE.TARGETED_WITH_CONSENT:
			_init_targeted_with_consent()
		INIT_TYPE.TARGETED_WITHOUT_CONSENT:
			_init_targeted_without_consent()
		INIT_TYPE.NON_TARGETED_WITHOUT_CONSENT:
			_init_non_targeted_without_consent()
		_:
			_init_targeted_with_consent()


func _init_targeted_with_consent() -> void:
	if privacy_policy_url.empty():
		print("Specify valid url for privacy policy in order to init admob using consent and targeted")
		return
	if publisher_ids.empty():
		print("Add at least on publisher id in order to init admob using consent and targeted")
		return
	_admob_singleton.initTargetedWithConsent(is_for_child_directed_treatment, is_under_age_of_consent, max_ad_content_rate, test_devices_id_request_configuration, publisher_ids, privacy_policy_url)


func _init_targeted_without_consent() -> void:
	_admob_singleton.initTargetedWithoutConsent(is_for_child_directed_treatment, is_under_age_of_consent, max_ad_content_rate, test_devices_id_request_configuration)


func _init_non_targeted_without_consent() -> void:
	_admob_singleton.initNonTargetedWithoutConsent()


func load_banner() -> void:
	if _admob_singleton:
		_admob_singleton.loadBannerAd(banner_ad_unit_id, is_banner_ad_on_top)


func show_banner() -> void:
	if _admob_singleton:
		_admob_singleton.showBannerAd()


func hide_banner() -> void:
	if _admob_singleton:
		_admob_singleton.hideBannerAd()


func resize_banner() -> void:
	if _admob_singleton:
		_admob_singleton.resizeBannerAd()


func load_interstitial() -> void:
	if _admob_singleton:
		_admob_singleton.loadInterstitialAd(interstitial_ad_unit_id)


func show_interstitial() -> void:
	if _admob_singleton:
		_admob_singleton.showInterstitialAd()


func load_rewarded_ads(rewarded_ad_ids: Array) -> void:
	if _admob_singleton:
		_admob_singleton.loadRewardedAds(rewarded_ad_ids)


func load_rewarded_ad(rewarded_ad_id: String) -> void:
	if _admob_singleton:
		_admob_singleton.loadRewardedAd(rewarded_ad_id)


func show_rewarded_ad(rewarded_ad_id: String) -> void:
	if _admob_singleton:
		_admob_singleton.showRewardedAd(rewarded_ad_id)


func show_consent_info() -> void:
	if _admob_singleton:
		_admob_singleton.showConsentInfo()


func revoke_consent_info() -> void:
	if _admob_singleton:
		_admob_singleton.revokeConsentInfo()


func set_ads_volume(volume: float) -> void:
	if _admob_singleton:
		_admob_singleton.setAdVolume(ad_volume)


# Callbacks
func _on_ads_initialized() -> void:
	emit_signal("on_ads_initialized")


func _on_banner_ad_loaded() -> void:
	emit_signal("on_banner_ad_loaded")


func _on_banner_ad_failed_to_load(error_code: int) -> void:
	emit_signal("on_banner_ad_failed_to_load", error_code)


func _on_banner_ad_opened() -> void:
	emit_signal("on_banner_ad_opened")


func _on_banner_ad_clicked() -> void:
	emit_signal("on_banner_ad_clicked")


func _on_banner_ad_left_app() -> void:
	emit_signal("on_banner_ad_left_app")


func _on_banner_ad_closed() -> void:
	emit_signal("on_banner_ad_closed")


func _on_interstitial_ad_loaded() -> void:
	emit_signal("on_interstitial_ad_loaded")


func _on_interstitial_ad_failed_to_load(error_code: int) -> void:
	emit_signal("on_interstitial_ad_failed_to_load", error_code)


func _on_interstitial_ad_opened() -> void:
	emit_signal("on_interstitial_ad_opened")


func _on_interstitial_ad_clicked() -> void:
	emit_signal("on_interstitial_ad_clicked")


func _on_interstitial_ad_left_app() -> void:
	emit_signal("on_interstitial_ad_left_app")


func _on_interstitial_ad_closed() -> void:
	emit_signal("on_interstitial_ad_closed")


func _on_interstitial_ad_not_loaded_yet() -> void:
	emit_signal("on_interstitial_ad_not_loaded_yet")


func _on_rewarded_ad_loaded(unit_id: String) -> void:
	emit_signal("on_rewarded_ad_loaded", unit_id)


func _on_rewarded_ad_failed_to_load(unit_id: String, error_code: int) -> void:
	emit_signal("on_rewarded_ad_failed_to_load", unit_id, error_code)


func _on_rewarded_ad_not_loaded(unit_id: String) -> void:
	emit_signal("on_rewarded_ad_not_loaded", unit_id)


func _on_rewarded_ad_opened(unit_id: String) -> void:
	emit_signal("on_rewarded_ad_opened", unit_id)


func _on_rewarded_ad_closed(unit_id: String) -> void:
	emit_signal("on_rewarded_ad_closed", unit_id)


func _on_rewarded_ad_earned(unit_id: String, type: String, amount: int) -> void:
	emit_signal("on_rewarded_ad_earned", unit_id, type, amount)


func _on_rewarded_ad_failed_to_show(unit_id: String, error_code: int) -> void:
	emit_signal("on_rewarded_ad_failed_to_show", unit_id, error_code)


func _on_consent_ads_allowed() -> void:
	emit_signal("on_consent_ads_allowed")


func _on_consent_form_loaded() -> void:
	emit_signal("on_consent_form_loaded")


func _on_consent_form_opened() -> void:
	emit_signal("on_consent_form_opened")


func _on_consent_user_prefer_ad_free() -> void:
	emit_signal("on_consent_user_prefer_ad_free")


func _on_consent_eea_user_unkown() -> void:
	emit_signal("on_consent_eea_user_unkown")


func _on_consent_form_error(error: String) -> void:
	emit_signal("on_consent_form_error", error)


func _on_consent_info_updated() -> void:
	emit_signal("on_consent_info_updated")


func _on_consent_info_failed_to_update(error: String) -> void:
	emit_signal("on_consent_info_failed_to_update", error)
