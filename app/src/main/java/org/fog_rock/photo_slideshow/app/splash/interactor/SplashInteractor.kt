package org.fog_rock.photo_slideshow.app.splash.interactor

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.fog_rock.photo_slideshow.app.module.lib.AppDatabase
import org.fog_rock.photo_slideshow.app.module.lib.GoogleWebApis
import org.fog_rock.photo_slideshow.app.splash.contract.SplashContract
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.viper.ViperContract
import java.util.concurrent.CancellationException

class SplashInteractor(
    private val context: Context,
    private val appDatabase: AppDatabase,
    private val googleWebApis: GoogleWebApis
): ViewModel(), SplashContract.Interactor {

    private var callback: SplashContract.InteractorCallback? = null

    override fun create(callback: ViperContract.InteractorCallback) {
        if (callback is SplashContract.InteractorCallback) {
            this.callback = callback
        } else {
            IllegalArgumentException("SplashContract.InteractorCallback should be set.")
        }
    }

    override fun destroy() {
        viewModelScope.cancel(CancellationException("Destroy method is called."))
        callback = null
    }

    override fun requestGoogleSilentSignIn() {
        viewModelScope.launch(Dispatchers.Default) {
            logI("requestGoogleSilentSignIn: Start coroutine.")
            val result = googleWebApis.requestSilentSignIn()
            withContext(Dispatchers.Main) {
                callback?.requestGoogleSilentSignInResult(result)
            }
            logI("requestGoogleSilentSignIn: End coroutine.")
        }
    }

    override fun requestUpdateUserInfo() {
        viewModelScope.launch(Dispatchers.Default) {
            logI("requestUpdateUserInfo: Start coroutine.")
            val result = updateUserInfo()
            withContext(Dispatchers.Main) {
                callback?.requestUpdateUserInfoResult(result)
            }
            logI("requestUpdateUserInfo: End coroutine.")
        }
    }

    override fun isGrantedRuntimePermissions(permissions: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            logI("OS version is less than M.")
            return true
        }
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                logI("All runtime permissions are not granted.")
                return false
            }
        }
        logI("All runtime permissions are granted.")
        return true
    }

    override fun isSucceededGoogleUserSignIn(data: Intent?): Boolean =
        googleWebApis.isSucceededUserSignIn(data)

    private suspend fun updateUserInfo(): Boolean {
        val emailAddress = googleWebApis.getSignedInEmailAddress()
        val userInfo = appDatabase.findUserInfoByEmailAddress(emailAddress)
        val tokenInfo = googleWebApis.requestUpdateTokenInfo(userInfo?.tokenInfo())

        return if (tokenInfo != null) {
            logI("Succeeded to update TokenInfo. Update database.")
            appDatabase.updateUserInfo(emailAddress, tokenInfo)
            true
        } else {
            // 失敗した場合はGoogleアカウントアクセス破棄をする.
            logI("Failed to update TokenInfo. Revoke access.")
            googleWebApis.requestSignOut(true)
            false
        }
    }
}