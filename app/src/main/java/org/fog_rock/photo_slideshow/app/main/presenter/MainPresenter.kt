package org.fog_rock.photo_slideshow.app.main.presenter

import android.app.Activity
import android.content.Intent
import com.google.photos.types.proto.Album
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.main.contract.MainContract
import org.fog_rock.photo_slideshow.app.main.entity.UpdatePhotosRequest
import org.fog_rock.photo_slideshow.app.module.AppDatabase
import org.fog_rock.photo_slideshow.app.select.entity.SelectAlbumsResult
import org.fog_rock.photo_slideshow.core.database.entity.DisplayedPhoto
import org.fog_rock.photo_slideshow.core.extension.getArrayListExtra
import org.fog_rock.photo_slideshow.core.extension.logE
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.viper.ViperContract
import org.fog_rock.photo_slideshow.core.webapi.entity.ApiResult

class MainPresenter(
    private var interactor: MainContract.Interactor?,
    private var router: MainContract.Router?
) : MainContract.Presenter, MainContract.InteractorCallback {

    private var callback: MainContract.PresenterCallback? = null

    override fun create(callback: ViperContract.PresenterCallback) {
        if (callback is MainContract.PresenterCallback) {
            this.callback = callback
            interactor?.create(this)
        } else {
            IllegalArgumentException("MainContract.PresenterCallback should be set.")
        }
    }

    override fun destroy() {
        interactor?.destroy()
        interactor = null
        router = null
        callback = null
    }

    override fun requestLoadDisplayedPhotos() {
        interactor?.requestLoadDisplayedPhotos()
    }

    override fun requestUpdateDisplayedPhotos() {
        if (!(interactor?.isNeededUpdatePhotos() ?: return)) {
            callback?.requestUpdateDisplayedPhotosResult(UpdatePhotosRequest.CONFIG_UPDATE)
            return
        }
        if (!(interactor?.hasSelectedAlbums() ?: return)) {
            router?.startSelectActivity((activity() ?: return), UpdatePhotosRequest.SELECT_ALBUMS.code)
            return
        }
        interactor?.requestDownloadPhotos()
    }

    override fun requestShowLicenses() {
        router?.startOssLicensesMenuActivity((activity() ?: return), R.string.license)
    }

    override fun requestSignOut() {
        interactor?.requestSignOut()
    }

    override fun evaluateActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        logI("evaluateActivityResult() " +
                "requestCode: $requestCode, resultCode: $resultCode")

        when (requestCode) {
            UpdatePhotosRequest.SELECT_ALBUMS.code -> {
                if (resultCode != Activity.RESULT_OK) {
                    callback?.requestUpdateDisplayedPhotosResult(UpdatePhotosRequest.SELECT_ALBUMS)
                    return
                }
                val albums = data?.getArrayListExtra<Album>(SelectAlbumsResult.DECIDED_ALBUMS.key()) ?: run {
                    callback?.requestUpdateDisplayedPhotosResult(UpdatePhotosRequest.SELECT_ALBUMS)
                    return
                }
                interactor?.requestDownloadPhotos(albums)
            }
            else -> {
                logE("Unknown requestCode: $requestCode")
                callback?.requestUpdateDisplayedPhotosResult(UpdatePhotosRequest.UNKNOWN)
            }
        }
    }

    override fun requestLoadDisplayedPhotosResult(displayedPhotos: List<DisplayedPhoto>) {
        callback?.requestLoadDisplayedPhotosResult(displayedPhotos)
    }

    override fun requestDownloadPhotosResult(photosInfo: List<AppDatabase.PhotoInfo>) {
        if (photosInfo.isNotEmpty()) {
            interactor?.requestUpdateDatabase(photosInfo)
        } else {
            callback?.requestUpdateDisplayedPhotosResult(UpdatePhotosRequest.DOWNLOAD_PHOTOS)
        }
    }

    override fun requestUpdateDatabaseResult(isSucceeded: Boolean) {
        if (isSucceeded) {
            callback?.requestUpdateDisplayedPhotosResult(UpdatePhotosRequest.COMPLETED)
        } else {
            callback?.requestUpdateDisplayedPhotosResult(UpdatePhotosRequest.UPDATE_DATABASE)
        }
    }

    override fun requestSignOutResult(result: ApiResult) {
        callback?.requestSignOutResult(result)
    }

    private fun activity(): Activity? = callback?.getActivity()
}