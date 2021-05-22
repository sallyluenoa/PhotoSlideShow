package org.fog_rock.photo_slideshow.app.menu.interactor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.fog_rock.photo_slideshow.app.menu.contract.MenuContract
import org.fog_rock.photo_slideshow.app.module.lib.AppDatabase
import org.fog_rock.photo_slideshow.app.module.lib.AppSettings
import org.fog_rock.photo_slideshow.app.module.lib.GoogleWebApis
import org.fog_rock.photo_slideshow.core.extension.downCast
import org.fog_rock.photo_slideshow.core.viper.ViperContract
import org.fog_rock.photo_slideshow.core.webapi.entity.ApiResult

class MenuInteractor(
    private val appSettings: AppSettings,
    private val appDatabase: AppDatabase,
    private val googleWebApis: GoogleWebApis
) : ViewModel(), MenuContract.Interactor {

    private var callback: MenuContract.InteractorCallback? = null

    override fun create(callback: ViperContract.InteractorCallback) {
        this.callback = callback.downCast()
            ?: throw IllegalArgumentException("MenuContract.InteractorCallback should be set.")
        createLoad()
    }

    override fun destroy() {
        callback = null
    }

    override fun requestChangeUser() {
        viewModelScope.launch(Dispatchers.Default) {
            val result = changeUser()
            withContext(Dispatchers.Main) {
                callback?.onChangeUserResult(result)
            }
        }
    }

    override fun requestSignOut() {
        viewModelScope.launch(Dispatchers.Default) {
            val result = signOut()
            withContext(Dispatchers.Main) {
                callback?.onSignOutResult(result)
            }
        }
    }

    private fun createLoad() {
        val account = googleWebApis.getSignedInAccount()
        callback?.onCreateResult(
            account.displayName ?: "",
            account.email ?: "",
            appSettings.getNumberOfPhotos(),
            appSettings.getTimeIntervalOfPhotos(),
            appSettings.getServerUpdateTime()
        )
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