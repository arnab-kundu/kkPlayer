package com.akundu.kkplayer.ui

object TestTags {
    const val MAIN_SONG_LIST = "main_songList"
    const val MAIN_SONG_ITEM_ROW = "main_songItem_row"
    const val MAIN_SONG_ITEM_ACTION_BUTTON = "main_songItem_playOrDownloadButton"

    const val PLAYER_ALBUM_ART = "player_albumArt"
    const val PLAYER_SEEK_BAR = "player_seekBar"
    const val PLAYER_VOLUME_SWIPE_ZONE = "player_volumeSwipeZone"
    const val PLAYER_PREVIOUS_BUTTON = "player_previousButton"
    const val PLAYER_PLAY_PAUSE_BUTTON = "player_playPauseButton"
    const val PLAYER_NEXT_BUTTON = "player_nextButton"
    const val PLAYER_BACK_BUTTON = "player_backButton"
    const val PLAYER_SETTINGS_BUTTON = "player_settingsButton"

    const val PLAYER_INFO_DIALOG_OK_BUTTON = "playerInfoDialog_okButton"
    const val PLAYER_INFO_DIALOG_CANCEL_BUTTON = "playerInfoDialog_cancelButton"

    const val SETTINGS_BACK_BUTTON = "settings_backButton"
    const val SETTINGS_CLEAR_CACHE_BUTTON = "settings_clearCacheButton"
    const val SETTINGS_CLEAR_DATABASE_BUTTON = "settings_clearDatabaseButton"
    const val SETTINGS_CLEAR_ALL_DATA_BUTTON = "settings_clearAllDataButton"

    const val SPLASH_LOGO = "splash_logo"
    const val SPLASH_VERSION_TEXT = "splash_versionText"
    const val SPLASH_LOADING_DOTS = "splash_loadingDots"
    const val SPLASH_EMAIL_FIELD = "splash_emailField"
    const val SPLASH_PASSWORD_FIELD = "splash_passwordField"
    const val SPLASH_BIOMETRIC_SWITCH = "splash_biometricSwitch"
    const val SPLASH_LOGIN_BUTTON = "splash_loginButton"
    const val SPLASH_FORGOT_PASSWORD_TEXT = "splash_forgotPasswordText"

    fun settingsTheme(theme: String): String = "settings_theme_$theme"

    fun settingsRepeatMode(mode: String): String = "settings_repeat_$mode"

    fun settingsDisplayOption(option: String): String = "settings_display_$option"
}
