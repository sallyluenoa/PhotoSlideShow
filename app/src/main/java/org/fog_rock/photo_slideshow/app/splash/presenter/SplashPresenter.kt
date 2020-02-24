package org.fog_rock.photo_slideshow.app.splash.presenter

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.util.Log
import org.fog_rock.photo_slideshow.app.splash.contract.SplashContract
import org.fog_rock.photo_slideshow.app.splash.interactor.SplashInteractor
import org.fog_rock.photo_slideshow.app.splash.router.SplashRouter
import org.fog_rock.photo_slideshow.core.entity.PhotoScope
import org.fog_rock.photo_slideshow.core.entity.SignInRequest

class SplashPresenter(private val callback: SplashContract.PresenterCallback): SplashContract.Presenter {

    private val TAG = SplashPresenter::class.java.simpleName

    private val interactor: SplashContract.Interactor = SplashInteractor(activity().applicationContext)
    private val router: SplashContract.Router = SplashRouter()

    override fun destroy() {
        interactor.destroy()
    }

    override fun requestSignIn() {
        presentSequence(SignInRequest.RUNTIME_PERMISSIONS)
    }

    override fun evaluateActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i(TAG, "evaluateActivityResult() " +
                "requestCode: $requestCode, resultCode: $resultCode")

        when (requestCode) {
            SignInRequest.GOOGLE_SIGN_IN.code -> {
                if (interactor.isSignedInGoogle(data)) {
                    Log.i(TAG, "Succeeded google sign in.")
                    presentSequence(SignInRequest.COMPLETED)
                } else {
                    Log.i(TAG, "Failed google sign in.")
                    callback.failedSignIn(SignInRequest.GOOGLE_SIGN_IN)
                }
            }
            else -> {
                Log.e(TAG, "Unknown requestCode: $requestCode")
            }
        }
    }

    override fun evaluateRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Log.i(TAG, "evaluateRequestPermissionsResult() requestCode: $requestCode")

        when (requestCode) {
            SignInRequest.RUNTIME_PERMISSIONS.code -> {
                if (interactor.isGrantedRuntimePermissions(permissions)) {
                    Log.i(TAG, "Runtime permissions is granted.")
                    presentSequence(SignInRequest.GOOGLE_SIGN_IN)
                } else {
                    Log.i(TAG, "Runtime permissions is denied.")
                    callback.failedSignIn(SignInRequest.RUNTIME_PERMISSIONS)
                }
            }
            else -> {
                Log.e(TAG, "Unknown requestCode: $requestCode")
            }
        }
    }

    private fun activity(): Activity = callback.getActivity()

    /**
     * リクエストに応じたサインインシーケンスを行う.
     * @param request サインインリクエスト
     */
    private fun presentSequence(request: SignInRequest) {
        if (request <= SignInRequest.RUNTIME_PERMISSIONS) {
            Log.i(TAG, "Check runtime permissions.")
            if (presentRuntimePermissions()) {
                Log.i(TAG, "Presented runtime permissions.")
                return
            }
        }
        if (request <= SignInRequest.GOOGLE_SIGN_IN) {
            Log.i(TAG, "Check google sign in.")
            if (presentGoogleSignIn()) {
                Log.i(TAG, "Presented google sign in.")
                return
            }
        }
        presentMainActivity()
        Log.i(TAG, "Presented main activity.")
    }

    /**
     * ランタイムパーミッション許可の表示.
     * @return 許可確認を表示した場合はtrue, そうでない場合はfalse
     */
    private fun presentRuntimePermissions(): Boolean {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (interactor.isGrantedRuntimePermissions(permissions)) {
            Log.i(TAG, "Runtime permissions are granted.")
            return false
        }
        Log.i(TAG, "Request runtime permissions.")
        router.startRuntimePermissions(activity(), permissions, SignInRequest.RUNTIME_PERMISSIONS.code)
        return true
    }

    /**
     * Googleサインインの表示.
     * @return サインインを表示した場合はtrue, そうでない場合はfalse
     */
    private fun presentGoogleSignIn(): Boolean {
        if (interactor.isSignedInGoogle()) {
            Log.i(TAG, "Signed in with google account.")
            return false
        }
        Log.i(TAG, "Request google sign in.")
        val scopes = arrayOf(PhotoScope.READ_ONLY)
        router.startGoogleSignInActivity(activity(), interactor.getClientHolder(scopes), SignInRequest.GOOGLE_SIGN_IN.code)
        return true
    }

    /**
     * メイン画面の表示.
     */
    private fun presentMainActivity() {
        router.startMainActivity(activity())
        callback.succeededSignIn()
    }
}