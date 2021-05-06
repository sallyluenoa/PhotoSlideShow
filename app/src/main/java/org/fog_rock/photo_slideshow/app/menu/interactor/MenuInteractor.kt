package org.fog_rock.photo_slideshow.app.menu.interactor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.fog_rock.photo_slideshow.app.menu.contract.MenuContract
import org.fog_rock.photo_slideshow.app.module.lib.GoogleWebApis
import org.fog_rock.photo_slideshow.core.viper.ViperContract

class MenuInteractor(
    private val googleWebApis: GoogleWebApis
) : ViewModel(), MenuContract.Interactor {

    private var callback: MenuContract.InteractorCallback? = null

    override fun create(callback: ViperContract.InteractorCallback) {
        if (callback is MenuContract.InteractorCallback) {
            this.callback = callback
            createLoad()
        } else {
            throw IllegalArgumentException("MenuContract.InteractorCallback should be set.")
        }
    }

    override fun destroy() {
        callback = null
    }

    private fun createLoad() {
        viewModelScope.launch(Dispatchers.Default) {
            val account = googleWebApis.getSignedInAccount()
            val accountName = account.displayName ?: ""
            val emailAddress = account.email ?: ""
            withContext(Dispatchers.Main) {
                callback?.createLoadResult(accountName, emailAddress)
            }
        }
    }
}