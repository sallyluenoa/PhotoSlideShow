package org.fog_rock.photo_slideshow.app.main.entity

/**
 * 写真更新シーケンスにおけるリクエスト.
 */
enum class UpdatePhotosRequest(val code: Int) {
    /**
     * 更新確認.
     */
    CONFIG_UPDATE(1000),

    /**
     * アルバム選択.
     */
    SELECT_ALBUMS(1001),

    /**
     * 写真ダウンロード.
     */
    DOWNLOAD_PHOTOS(1010),

    /**
     * データベース更新.
     */
    UPDATE_DATABASE(1100),

    /**
     * 完了.
     */
    COMPLETED(1111),

    /**
     * 不明.
     */
    UNKNOWN(9999),
    ;

    companion object {
        /**
         * コードナンバーからリクエストへコンバートする.
         */
        fun convertFromCode(code: Int): UpdatePhotosRequest =
            values().find { it.code == code } ?: UNKNOWN
    }

    /**
     * 次のシーケンスリクエストを取得する.
     */
    fun next(): UpdatePhotosRequest = when (this) {
        CONFIG_UPDATE -> SELECT_ALBUMS
        SELECT_ALBUMS -> DOWNLOAD_PHOTOS
        DOWNLOAD_PHOTOS -> UPDATE_DATABASE
        UPDATE_DATABASE -> COMPLETED
        else -> UNKNOWN
    }

}