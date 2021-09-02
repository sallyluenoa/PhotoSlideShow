package org.fog_rock.photo_slideshow.core.webapi.entity

/**
 * APIの取得結果.
 */
enum class ApiResult {
    /**
     * 成功した.
     */
    SUCCEEDED,

    /**
     * 失敗した.
     */
    FAILED,

    /**
     * キャンセルした.
     */
    CANCELED,

    /**
     * 無効.
     */
    INVALID;
}