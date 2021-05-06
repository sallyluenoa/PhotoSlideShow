package org.fog_rock.photo_slideshow.app.menu.presenter

import android.app.Activity
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.menu.contract.MenuContract
import org.fog_rock.photo_slideshow.core.viper.ViperContract
import org.fog_rock.photo_slideshow.core.webapi.entity.ApiResult

class MenuPresenter(
    private var interactor: MenuContract.Interactor?,
    private var router: MenuContract.Router?
) : MenuContract.Presenter, MenuContract.InteractorCallback {

    private var callback: MenuContract.PresenterCallback? = null

    override fun create(callback: ViperContract.PresenterCallback) {
        if (callback is MenuContract.PresenterCallback) {
            this.callback = callback
            interactor?.create(this)
        } else {
            throw IllegalArgumentException("MenuContract.PresenterCallback should be set.")
        }
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

    override fun createLoadResult(accountName: String, emailAddress: String) {
        callback?.createLoadResult(accountName, emailAddress)
    }

    override fun requestChangeUserResult(result: ApiResult) {
        callback?.requestChangeUserResult(result)
    }

    override fun requestSignOutResult(result: ApiResult) {
        callback?.requestSignOutResult(result)
    }

    private fun activity(): Activity? = callback?.getActivity()
}