package org.fog_rock.photo_slideshow.app.splash.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.module.lib.impl.AppDatabaseImpl
import org.fog_rock.photo_slideshow.app.module.ui.AppDialogFragment
import org.fog_rock.photo_slideshow.app.module.ui.AppSimpleFragment
import org.fog_rock.photo_slideshow.app.module.lib.impl.GoogleWebApisImpl
import org.fog_rock.photo_slideshow.app.splash.contract.SplashContract
import org.fog_rock.photo_slideshow.app.splash.entity.SignInRequest
import org.fog_rock.photo_slideshow.app.splash.interactor.SplashInteractor
import org.fog_rock.photo_slideshow.app.splash.presenter.SplashPresenter
import org.fog_rock.photo_slideshow.app.splash.router.SplashRouter
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleOAuth2ApiImpl
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleSignInApiImpl
import org.fog_rock.photo_slideshow.core.webapi.impl.PhotosLibraryApiImpl

class SplashActivity : AppCompatActivity(), SplashContract.PresenterCallback, AppDialogFragment.Callback {

    companion object {
        private const val DISPLAY_LOGO_TIME_MILLIS = 2000L
    }

    private val fragmentManager = supportFragmentManager

    private var presenter: SplashContract.Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)
        replaceFragment(
            AppSimpleFragment.newInstance(
                AppSimpleFragment.Layout.LOGO))

        presenter = SplashPresenter(
            SplashInteractor(
                AppDatabaseImpl(),
                GoogleWebApisImpl(this, GoogleSignInApiImpl(), GoogleOAuth2ApiImpl(), PhotosLibraryApiImpl())
            ),
            SplashRouter()
        )
        presenter?.create(this)

        lifecycleScope.launch(Dispatchers.Main) {
            delay(DISPLAY_LOGO_TIME_MILLIS)
            replaceFragment(
                AppSimpleFragment.newInstance(
                    AppSimpleFragment.Layout.EMPTY))
            requestSignIn()
        }
    }

    override fun onDestroy() {
        presenter?.destroy()
        presenter = null

        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        presenter?.evaluateActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        presenter?.evaluateRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDialogResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == AppDialogFragment.BUTTON_POSITIVE) {
            requestSignIn()
        } else {
            finish()
        }
    }

    override fun getActivity(): Activity = this

    override fun requestSignInResult(request: SignInRequest) {
        logI("requestSignInResult: $request")

        if (request == SignInRequest.COMPLETED) {
            finish()
        } else {
            AppDialogFragment.Builder(this).apply {
                setTitle(request.failedTitle)
                setMessage(request.failedMessage)
                setPositiveLabel(R.string.retry)
                setNegativeLabel(R.string.cancel)
                setCancelable(false)
            }.show(fragmentManager, request.code)
        }
    }

    /**
     * 新しいフラグメントに置換する.
     */
    private fun replaceFragment(fragment: Fragment) {
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
        }.commit()
    }

    /**
     * サインインを要求する.
     */
    private fun requestSignIn() {
        logI("Request sign in.")
        presenter?.requestSignIn()
    }
}