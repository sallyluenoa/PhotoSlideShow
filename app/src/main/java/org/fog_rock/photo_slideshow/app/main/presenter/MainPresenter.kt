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
                    logE("ResultCode is not OK.")
                    callback?.requestUpdateDisplayedPhotosResult(request)
                    return
                }
                val albums = data?.getArrayListExtra<Album>(SelectAlbumsResult.DECIDED_ALBUMS.key()) ?: run {
                    logE("Failed to get albums from intent.")
                    callback?.requestUpdateDisplayedPhotosResult(request)
                    return
                }
                logI("Succeeded to get albums selected by user.")
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
            logI("Succeeded to download photos.")
            presentSequence(request.next(), photosInfo)
        } else {
            logE("Failed to download photos.")
            callback?.requestUpdateDisplayedPhotosResult(request)
        }
    }

    override fun requestUpdateDatabaseResult(isSucceeded: Boolean) {
        val request = UpdatePhotosRequest.UPDATE_DATABASE
        if (isSucceeded) {
            logI("Succeeded to update database.")
            presentSequence(request.next())
        } else {
            logE("Failed to update database.")
            callback?.requestUpdateDisplayedPhotosResult(request)
        }
    }

    override fun requestSignOutResult(result: ApiResult) {
        callback?.requestSignOutResult(result)
    }

    private fun activity(): Activity? = callback?.getActivity()

    /**
     * リクエストに応じた写真更新シーケンスを行う.
     * @param request リクエスト
     */
    private fun presentSequence(request: UpdatePhotosRequest, value: Any? = null) {
        when (request) {
            UpdatePhotosRequest.CONFIG_UPDATE -> {
                if (interactor?.isNeededUpdatePhotos() ?: return) {
                    logI("Needed to update photos.")
                    presentSequence(request.next())
                } else {
                    logI("No needed to update photos.")
                    callback?.requestUpdateDisplayedPhotosResult(request)
                }
            }
            UpdatePhotosRequest.SELECT_ALBUMS -> {
                if (interactor?.hasSelectedAlbums() ?: return) {
                    logI("Albums are already selected.")
                    presentSequence(request.next())
                } else {
                    logI("Albums are not selected. Start SelectActivity.")
                    router?.startSelectActivity((activity() ?: return), request.code)
                }
            }
            UpdatePhotosRequest.DOWNLOAD_PHOTOS -> {
                logI("Request download photos.")
                val albums = value.downCast<List<Album>>()
                interactor?.requestDownloadPhotos((activity() ?: return), albums)
            }
            UpdatePhotosRequest.UPDATE_DATABASE -> {
                val photosInfo = value.downCast<List<AppDatabase.PhotoInfo>>()
                if (photosInfo != null) {
                    logI("Request update database.")
                    interactor?.requestUpdateDatabase(photosInfo)
                } else {
                    logE("PhotosInfo is null.")
                    callback?.requestUpdateDisplayedPhotosResult(request)
                }
            }
            UpdatePhotosRequest.COMPLETED -> {
                logI("All requests are completed.")
                callback?.requestUpdateDisplayedPhotosResult(request)
            }
            else -> {
                logE("Unknown request: $request")
                callback?.requestUpdateDisplayedPhotosResult(UpdatePhotosRequest.UNKNOWN)
            }
        }
    }
}