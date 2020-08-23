package org.fog_rock.photo_slideshow.app.main.entity

enum class UpdatePhotosRequest(val code: Int) {

    CONFIG_UPDATE(1000),

    SELECT_ALBUMS(1001),

    DOWNLOAD_PHOTOS(1010),

    UPDATE_DATABASE(1100),

    COMPLETED(1111),

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