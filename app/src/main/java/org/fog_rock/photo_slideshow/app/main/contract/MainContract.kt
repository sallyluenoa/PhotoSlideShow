package org.fog_rock.photo_slideshow.app.main.contract

import android.app.Activity
import android.content.Intent
import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem
import org.fog_rock.photo_slideshow.core.viper.ViperContract

class MainContract {

    interface Presenter : ViperContract.Presenter {
        /**
         * アルバム取得を要求.
         */
        fun requestAlbums()

        /**
         * ライセンス表示を要求.
         */
        fun requestLicense()

        /**
         * サインアウトを要求.
         */
        fun requestSignOut()

        /**
         * Activity#onActivityResult()の結果を評価する.
         * @see Activity.onActivityResult
         */
        fun evaluateActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    }

    interface PresenterCallback : ViperContract.PresenterCallback {
        /**
         * スライドショー開始を要求する.
         * @param files 画像ファイルリスト
         */
        fun requestSlideShow(files: List<String>)

        /**
         * ビューの終了処理を要求する.
         */
        fun requestFinish()
    }

    interface Interactor : ViperContract.Interactor {
        /**
         * 共有アルバム取得を要求.
         * @see InteractorCallback.requestSharedAlbumsResult
         */
        fun requestSharedAlbums()

        /**
         * メディアアイテム取得を要求.
         * @param album メディアアイテムを取得するターゲットアルバム
         */
        fun requestMediaItems(album: Album)

        /**
         * ファイルダウンロードを要求.
         * @param mediaItems ダウンロードターゲットのメディアアイテムリスト
         */
        fun requestDownloadFiles(mediaItems: List<MediaItem>)

        /**
         * サインアウトを要求.
         */
        fun requestSignOut()
    }

    interface InteractorCallback : ViperContract.InteractorCallback {
        /**
         * 共有アルバム取得結果.
         * @param albums アルバムリスト
         * @see Interactor.requestSharedAlbums
         */
        fun requestSharedAlbumsResult(albums: List<Album>?)

        /**
         * メディアアイテム取得結果.
         * @param mediaItems メディアアイテムリスト
         * @see Interactor.requestMediaItems
         */
        fun requestMediaItemsResult(mediaItems: List<MediaItem>?)

        /**
         * ファイルダウンロード完了.
         * @param files ダウンロードに成功した画像ファイルリスト
         * @see Interactor.requestDownloadFiles
         */
        fun completedDownloadFiles(files: List<String>)

        /**
         * サインアウト完了.
         */
        fun requestSignOutResult(isSucceeded: Boolean)
    }

    interface Router : ViperContract.Router {
        /**
         * SplashActivityの表示.
         */
        fun startSplashActivity(activity: Activity)

        /**
         * SelectActivityの表示.
         */
        fun startSelectActivity(activity: Activity, albums: List<Album>, requestCode: Int)

        /**
         * OssLicensesMenuActivityの表示.
         */
        fun startOssLicensesMenuActivity(activity: Activity, titleResId: Int)
    }
}