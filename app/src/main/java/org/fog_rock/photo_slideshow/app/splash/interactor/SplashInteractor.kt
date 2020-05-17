package org.fog_rock.photo_slideshow.app.splash.interactor

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.fog_rock.photo_slideshow.app.splash.contract.SplashContract
import org.fog_rock.photo_slideshow.core.webapi.GoogleSignInApi

class SplashInteractor(
    private val context: Context,
    private val signInApi: GoogleSignInApi,
    private val callback: SplashContract.InteractorCallback
): SplashContract.Interactor {

    private val TAG = SplashInteractor::class.java.simpleName

    override fun destroy() {
    }

    override fun requestGoogleSilentSignIn() {
        GlobalScope.launch(Dispatchers.Main) {
            val account = withContext(Dispatchers.Default) {
                signInApi.requestSilentSignIn()
            }
            callback.requestGoogleSilentSignInResult(account != null)
        }
    }

    override fun isGrantedRuntimePermissions(permissions: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.i(TAG, "OS version is less than M.")
            return true
        }
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "All runtime permissions are not granted.")
                return false
            }
        }
        Log.i(TAG, "All runtime permissions are granted.")
        return true
    }

    override fun isSucceededGoogleUserSignIn(data: Intent?): Boolean =
        GoogleSignInApi.isSucceededUserSignIn(data)
}