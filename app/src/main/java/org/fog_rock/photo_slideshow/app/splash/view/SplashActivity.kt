package org.fog_rock.photo_slideshow.app.splash.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.module.AppDialogFragment
import org.fog_rock.photo_slideshow.app.splash.contract.SplashContract
import org.fog_rock.photo_slideshow.app.splash.presenter.SplashPresenter
import org.fog_rock.photo_slideshow.core.entity.SignInRequest

class SplashActivity : AppCompatActivity(), SplashContract.PresenterCallback, AppDialogFragment.Callback {

    private val TAG = SplashActivity::class.java.simpleName

    private val SHOW_LOGO_TIME_MILLIS = 2000L

    private val fragmentManager = supportFragmentManager

    private lateinit var presenter: SplashContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)
        replaceFragment(LogoFragment.newInstance())

        presenter = SplashPresenter(this)

        Handler().postDelayed({
            Log.i(TAG, "Request sign in.")

            replaceFragment(SignInFragment.newInstance())
            presenter.requestSignIn()
        }, SHOW_LOGO_TIME_MILLIS)
    }

    override fun onDestroy() {
        presenter.destroy()

        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        presenter.evaluateActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        presenter.evaluateRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDialogResult(requestCode: Int, resultCode: Int, data: Intent) {
        finish()
    }

    override fun getActivity(): Activity = this

    override fun succeededSignIn() {
        Log.i(TAG, "Succeeded sign in.")
        finish()
    }

    override fun failedSignIn(request: SignInRequest) {
        Log.i(TAG, "Failed sign in.")
        AppDialogFragment.Builder(this).apply {
            setTitle(request.failedTitle)
            setMessage(request.failedMessage)
            setPositiveLabel(R.string.ok)
            setCancelable(false)
        }.show(fragmentManager, request.code)
    }

    /**
     * 新しいフラグメントに置換する.
     */
    private fun replaceFragment(fragment: Fragment) {
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
        }.commit()
    }
}
