package org.fog_rock.photo_slideshow.app.menu.presenter

import android.app.Activity
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.menu.contract.MenuContract
import org.fog_rock.photo_slideshow.core.viper.ViperContract

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
//        TODO("Not yet implemented")
    }

    override fun requestSignOut() {
//        TODO("Not yet implemented")
    }

    override fun createLoadResult(accountName: String, emailAddress: String) {
        callback?.createLoadResult(accountName, emailAddress)
    }

    private fun activity(): Activity? = callback?.getActivity()
}