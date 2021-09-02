package org.fog_rock.photo_slideshow.app.menu.presenter

import android.app.Activity
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.menu.contract.MenuContract
import org.fog_rock.photo_slideshow.core.extension.downCast
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.viper.ViperContract
import org.fog_rock.photo_slideshow.core.webapi.entity.ApiResult

class MenuPresenter(
    private var interactor: MenuContract.Interactor?,
    private var router: MenuContract.Router?
) : MenuContract.Presenter, MenuContract.InteractorCallback {

    private var callback: MenuContract.PresenterCallback? = null

    override fun create(callback: ViperContract.PresenterCallback) {
        this.callback = callback.downCast()
            ?: throw IllegalArgumentException("MenuContract.PresenterCallback should be set.")
        interactor?.create(this)
    }

    override fun destroy() {
        interactor?.destroy()
        interactor = null
        router = null
        callback = null
    }

    override fun requestShowLicenses() {
        router?.startOssLicensesMenuActivity((activity() ?: return), R.string.license_info)
    }

    override fun requestChangeUser() {
        interactor?.requestChangeUser()
    }

    override fun requestSignOut() {
        interactor?.requestSignOut()
    }

    override fun onCreateResult(
        accountName: String, emailAddress: String,
        numberOfPhotos: Int, timeIntervalOfPhotos: Int, serverUpdateTime: Int
    ) {
        callback?.onCreateResult(
            accountName, emailAddress, numberOfPhotos, timeIntervalOfPhotos, serverUpdateTime
        )
    }

    override fun onChangeUserResult(result: ApiResult) {
        logI("onChangeUserResult: $result")
        if (result == ApiResult.SUCCEEDED || result == ApiResult.INVALID) {
            logI("Start SplashActivity.")
            router?.startSplashActivityAndFinishAll(activity() ?: return)
        } else {
            logI("Failed to change user.")
            callback?.onFailedChangeUser()
        }
    }

    override fun onSignOutResult(result: ApiResult) {
        logI("onSignOutResult: $result")
        if (result == ApiResult.SUCCEEDED || result == ApiResult.INVALID) {
            logI("Start SplashActivity.")
            router?.startSplashActivityAndFinishAll(activity() ?: return)
        } else {
            logI("Failed to sign out.")
            callback?.onFailedSignOut()
        }
    }

    private fun activity(): Activity? = callback?.getActivity()
}