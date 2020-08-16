package org.fog_rock.photo_slideshow.core.viper

import android.app.Activity

/**
 * VIPERモジュールの規定クラス.
 */
class ViperContract {

    /**
     * Presenterインターフェース
     * View(Activity)からPresenterへ処理を受け渡す.
     */
    interface Presenter {
        /**
         * オブジェクトの初期処理. 主にCallbackを指定する.
         */
        fun create(callback: PresenterCallback)

        /**
         * オブジェクトの終了処理.
         * Activity#onDestroy() にて呼び出すこと.
         */
        fun destroy()
    }

    /**
     * PresenterCallbackインターフェース
     * PresenterからView(Activity)へ処理を受け渡す.
     * 非同期処理の結果等、Presenterメソッドの戻り値でリカバリーできない場合に使用.
     */
    interface PresenterCallback {
        /**
         * Activityを取得.
         */
        fun getActivity(): Activity
    }

    /**
     * Interactorインターフェース
     * PresenterからInteractorへ処理を受け渡す.
     */
    interface Interactor {
        /**
         * オブジェクトの初期処理. 主にCallbackを指定する.
         */
        fun create(callback: InteractorCallback)

        /**
         * オブジェクトの終了処理.
         * Activity#onDestroy() にて呼び出すこと.
         */
        fun destroy()
    }

    /**
     * InteractorCallbackインターフェース
     * InteractorからPresenterへ処理を受け渡す.
     * 非同期処理の結果等、Interactorメソッドの戻り値でリカバリーできない場合に使用.
     */
    interface InteractorCallback

    /**
     * Routerインターフェース
     * PresenterからRouterへ処理を受け渡す.
     */
    interface Router
}