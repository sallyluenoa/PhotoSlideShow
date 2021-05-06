package org.fog_rock.photo_slideshow.app.module.lib

/**
 * アプリ内の設定を取得するクラス.
 */
interface AppSettings {

    /**
     * 写真の表示枚数を取得.
     */
    fun getNumberOfPhotos(): Int

    /**
     * 写真の表示時間を取得.
     */
    fun getTimeIntervalOfPhotos(): Int

    /**
     * サーバーの更新時間を取得.
     */
    fun getServerUpdateTime(): Int
}