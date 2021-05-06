package org.fog_rock.photo_slideshow.app.menu.interactor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.fog_rock.photo_slideshow.app.menu.contract.MenuContract
import org.fog_rock.photo_slideshow.app.module.lib.AppDatabase
import org.fog_rock.photo_slideshow.app.module.lib.GoogleWebApis
import org.fog_rock.photo_slideshow.core.viper.ViperContract
import org.fog_rock.photo_slideshow.core.webapi.entity.ApiResult

class MenuInteractor(
    private val appDatabase: AppDatabase,
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

    override fun requestChangeUser() {
        viewModelScope.launch(Dispatchers.Default) {
            val result = changeUser()
            withContext(Dispatchers.Main) {
                callback?.requestChangeUserResult(result)
            }
        }
    }

    override fun requestSignOut() {
        viewModelScope.launch(Dispatchers.Default) {
            val result = signOut()
            withContext(Dispatchers.Main) {
                callback?.requestSignOutResult(result)
            }
        }
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

    private suspend fun changeUser(): ApiResult =
        googleWebApis.requestSignOut(false)

    private suspend fun signOut(): ApiResult {
        val result = googleWebApis.requestSignOut(true)
        if (result == ApiResult.SUCCEEDED) {
            val emailAddress = googleWebApis.getSignedInEmailAddress()
            appDatabase.deleteUserInfo(emailAddress)
        }
        return result
    }
}