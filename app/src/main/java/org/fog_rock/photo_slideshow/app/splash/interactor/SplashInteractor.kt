package org.fog_rock.photo_slideshow.app.splash.interactor

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import org.fog_rock.photo_slideshow.app.splash.contract.SplashContract
import org.fog_rock.photo_slideshow.core.entity.PhotoScope
import org.fog_rock.photo_slideshow.core.webapi.GoogleSignInApi
import org.fog_rock.photo_slideshow.core.webapi.GoogleSignInClientHolder

class SplashInteractor(private val context: Context): SplashContract.Interactor {

    private val TAG = SplashInteractor::class.java.simpleName

    private val signInApi = GoogleSignInApi(context)

    private var clientHolder: GoogleSignInClientHolder? = null

    override fun destroy() {
    }

    override fun getClientHolder(scopes: Array<PhotoScope>): GoogleSignInClientHolder =
        clientHolder ?: run {
            Log.i(TAG, "Generate new client holder.")
            clientHolder = GoogleSignInClientHolder(context, scopes)
            clientHolder!!
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

    override fun isSignedInGoogle(): Boolean = signInApi.isSignedIn()

    override fun isSignedInGoogle(data: Intent?): Boolean = signInApi.isSignedIn(data)
}