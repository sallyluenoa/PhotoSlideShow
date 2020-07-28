package org.fog_rock.photo_slideshow.app.main.presenter

import android.app.Activity.RESULT_OK
import android.content.Intent
import com.google.photos.types.proto.Album
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.main.contract.MainContract
import org.fog_rock.photo_slideshow.app.main.entity.UpdatePhotosRequest
import org.fog_rock.photo_slideshow.app.main.interactor.MainInteractor
import org.fog_rock.photo_slideshow.app.main.router.MainRouter
import org.fog_rock.photo_slideshow.app.select.view.SelectActivity
import org.fog_rock.photo_slideshow.core.extension.logE
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.webapi.entity.ApiResult

class MainPresenter(
    private val callback: MainContract.PresenterCallback
) : MainContract.Presenter, MainContract.InteractorCallback {

    private val interactor: MainContract.Interactor =
        MainInteractor(activity().applicationContext, this)

    private val router: MainContract.Router = MainRouter()

    override fun destroy() {
        interactor.destroy()
    }

    override fun requestUpdatePhotos() {
        presentSequence(UpdatePhotosRequest.LOAD_USER_INFO)
    }

    override fun requestShowLicenses() {
        router.startOssLicensesMenuActivity(activity(), R.string.license)
    }

    override fun requestSignOut() {
        interactor.requestSignOut()
    }

    override fun evaluateActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
             UpdatePhotosRequest.SELECT_ALBUMS.code -> {
                if (resultCode == RESULT_OK && data != null) {
                    logI("Succeeded to select album.")
                    val album = data.getSerializableExtra(SelectActivity.RESULT_DECIDE_ALBUM) as Album
                    interactor.requestUpdateSelectedAlbums(listOf(album))
                } else {
                    logI("Canceled to select album.")
                }
            }
            else -> {
                logE("Unknown requestCode: $requestCode")
                callback.requestUpdatePhotosResult(UpdatePhotosRequest.UNKNOWN)
            }
        }
    }

    override fun requestLoadFromDatabaseResult(isSucceeded: Boolean) {
        if (isSucceeded && interactor.isNeededUpdatePhotos()) {
            presentSequence(UpdatePhotosRequest.SELECT_ALBUMS)
        } else {
            callback.requestUpdatePhotosResult(UpdatePhotosRequest.LOAD_USER_INFO)
        }
    }

    override fun requestAlbumsResult(albums: List<Album>?) {
        if (albums != null && albums.isNotEmpty()) {
            router.startSelectActivity(activity(), albums, UpdatePhotosRequest.SELECT_ALBUMS.code)
        } else {
            callback.requestUpdatePhotosResult(UpdatePhotosRequest.SELECT_ALBUMS)
        }
    }

    override fun requestUpdateSelectedAlbumsResult(isSucceeded: Boolean) {
        if (isSucceeded) {
            presentSequence(UpdatePhotosRequest.DOWNLOAD_PHOTOS)
        } else {
            callback.requestUpdatePhotosResult(UpdatePhotosRequest.SELECT_ALBUMS)
        }
    }

    override fun requestDownloadPhotosResult(isSucceeded: Boolean) {
        if (isSucceeded) {
            presentSequence(UpdatePhotosRequest.COMPLETED)
        } else {
            callback.requestUpdatePhotosResult(UpdatePhotosRequest.DOWNLOAD_PHOTOS)
        }
    }

    override fun requestSignOutResult(result: ApiResult) {
        callback.requestSignOutResult(result);
    }

    private fun activity() = callback.getActivity()

    private fun presentSequence(request: UpdatePhotosRequest) {
        when (request) {
            UpdatePhotosRequest.SELECT_ALBUMS -> presentSelectAlbum()
            UpdatePhotosRequest.DOWNLOAD_PHOTOS -> presentDownloadPhotos()
            UpdatePhotosRequest.COMPLETED -> callback.requestUpdatePhotosResult(UpdatePhotosRequest.COMPLETED)
            else -> callback.requestUpdatePhotosResult(UpdatePhotosRequest.UNKNOWN)
        }
    }

    private fun presentSelectAlbum() {
        if (interactor.hasSelectedAlbums()) {
            presentSequence(UpdatePhotosRequest.DOWNLOAD_PHOTOS)
        } else {
            interactor.requestAlbums()
        }
    }

    private fun presentDownloadPhotos() {
        interactor.requestDownloadPhotos()
    }
}