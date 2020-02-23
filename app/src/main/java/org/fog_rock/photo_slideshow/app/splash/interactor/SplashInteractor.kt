package org.fog_rock.photo_slideshow.app.splash.interactor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import org.fog_rock.photo_slideshow.app.splash.contract.SplashContract

class SplashInteractor(
    private val context: Context
): SplashContract.Interactor, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private val TAG = SplashInteractor::class.java.simpleName

    private var client: GoogleApiClient? = null

    override fun destroy() {
    }

    override fun getGoogleApiClient(activity: Activity, scopes: Array<String>): GoogleApiClient =
        client ?: run {
            Log.i(TAG, "Generate new GoogleApiClient.")
            if (activity is FragmentActivity) {
                client = generateGoogleApiClient(activity, generateScope(scopes))
                client!!
            } else {
                throw IllegalArgumentException(
                    "Cannot generate GoogleApiClient. Activity is not extend FragmentActivity.")
            }
        }

    override fun isGrantedRuntimePermissions(permissions: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.i(TAG, "OS version is less than M.")
            return true
        }
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                Log.i(TAG, "All runtime permissions are not granted.")
                return false
        }
        Log.i(TAG, "All runtime permissions are granted.")
        return true
    }

    override fun isSignedInGoogle(): Boolean =
        (GoogleSignIn.getLastSignedInAccount(context) != null)

    override fun isSignedInGoogle(data: Intent?): Boolean {
        val data1 = data ?: run {
            Log.e(TAG, "Intent data is null.")
            return false
        }
        return Auth.GoogleSignInApi.getSignInResultFromIntent(data1).isSuccess
    }

    override fun onConnected(connectionHint: Bundle?) {
        Log.i(TAG, "onConnected")
    }

    override fun onConnectionSuspended(cause: Int) {
        Log.i(TAG, "onConnectionSuspended. cause: $cause")
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.i(TAG, "onConnectionFailed. " +
                "errorCode: ${connectionResult.errorCode}, errorMessage: ${connectionResult.errorMessage}")
    }

    private fun generateGoogleApiClient(activity: FragmentActivity, scope: Scope): GoogleApiClient =
        GoogleApiClient.Builder(context).apply {
            addConnectionCallbacks(this@SplashInteractor)
            enableAutoManage(activity, this@SplashInteractor)
            addScope(scope)
            addApi(Auth.GOOGLE_SIGN_IN_API, generateGoogleSignInOptions(scope))
        }.build()

    private fun generateGoogleSignInOptions(scope: Scope): GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).apply {
            requestScopes(scope)
            requestEmail()
        }.build()

    private fun generateScope(scopes: Array<String>): Scope {
        var tmp = ""
        scopes.forEach {
            if (tmp.isEmpty()) tmp += " "
            tmp += it
        }
        return Scope(tmp)
    }
}