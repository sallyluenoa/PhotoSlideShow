package org.fog_rock.photo_slideshow.app.main.presenter

import android.app.Activity
import android.content.Intent
import com.google.photos.types.proto.Album
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.main.contract.MainContract
import org.fog_rock.photo_slideshow.app.main.entity.UpdatePhotosRequest
import org.fog_rock.photo_slideshow.app.module.lib.AppDatabase
import org.fog_rock.photo_slideshow.app.select.entity.SelectAlbumsResult
import org.fog_rock.photo_slideshow.core.database.entity.DisplayedPhoto
import org.fog_rock.photo_slideshow.core.extension.downCast
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
        presentSequence(UpdatePhotosRequest.CONFIG_UPDATE)
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

        val request = UpdatePhotosRequest.convertFromCode(requestCode)
        when (request) {
            UpdatePhotosRequest.SELECT_ALBUMS -> {
                if (resultCode != Activity.RESULT_OK) {
                    callback?.requestUpdateDisplayedPhotosResult(request)
                    return
                }
                val albums = data?.getArrayListExtra<Album>(SelectAlbumsResult.DECIDED_ALBUMS.key()) ?: run {
                    callback?.requestUpdateDisplayedPhotosResult(request)
                    return
                }
                presentSequence(request.next(), albums)
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
        val request = UpdatePhotosRequest.DOWNLOAD_PHOTOS
        if (photosInfo.isNotEmpty()) {
            presentSequence(request.next(), photosInfo)
        } else {
            callback?.requestUpdateDisplayedPhotosResult(request)
        }
    }

    override fun requestUpdateDatabaseResult(isSucceeded: Boolean) {
        val request = UpdatePhotosRequest.UPDATE_DATABASE
        if (isSucceeded) {
            presentSequence(request.next())
        } else {
            callback?.requestUpdateDisplayedPhotosResult(request)
        }
    }

    override fun requestSignOutResult(result: ApiResult) {
        callback?.requestSignOutResult(result)
    }

    private fun activity(): Activity? = callback?.getActivity()

    private fun presentSequence(request: UpdatePhotosRequest, value: Any? = null) {
        when (request) {
            UpdatePhotosRequest.CONFIG_UPDATE -> {
                if (interactor?.isNeededUpdatePhotos() ?: return) {
                    presentSequence(request.next())
                } else {
                    callback?.requestUpdateDisplayedPhotosResult(request)
                }
            }
            UpdatePhotosRequest.SELECT_ALBUMS -> {
                if (interactor?.hasSelectedAlbums() ?: return) {
                    presentSequence(request.next())
                } else {
                    router?.startSelectActivity((activity() ?: return), request.code)
                }
            }
            UpdatePhotosRequest.DOWNLOAD_PHOTOS -> {
                val albums = value.downCast<List<Album>>()
                interactor?.requestDownloadPhotos((activity() ?: return), albums)
            }
            UpdatePhotosRequest.UPDATE_DATABASE -> {
                val photosInfo = value.downCast<List<AppDatabase.PhotoInfo>>()
                if (photosInfo != null) {
                    interactor?.requestUpdateDatabase(photosInfo)
                } else {
                    callback?.requestUpdateDisplayedPhotosResult(request)
                }
            }
            UpdatePhotosRequest.COMPLETED -> {
                callback?.requestUpdateDisplayedPhotosResult(request)
            }
            else -> {
                callback?.requestUpdateDisplayedPhotosResult(UpdatePhotosRequest.UNKNOWN)
            }
        }
    }
}