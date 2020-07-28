package org.fog_rock.photo_slideshow.app.main.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.main.contract.MainContract
import org.fog_rock.photo_slideshow.app.main.entity.UpdatePhotosRequest
import org.fog_rock.photo_slideshow.app.main.presenter.MainPresenter
import org.fog_rock.photo_slideshow.app.module.AppSimpleFragment
import org.fog_rock.photo_slideshow.core.extension.logE
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.webapi.entity.ApiResult

class MainActivity : AppCompatActivity(), MainContract.PresenterCallback {

    companion object {
        private const val PRESENT_TIME_MILLISECS = 5000L
    }

    private val fragmentManager = supportFragmentManager

    private val handler = Handler()

    private lateinit var presenter: MainContract.Presenter

    private var slideShowFiles = listOf<String>()
    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        replaceFragment(AppSimpleFragment.newInstance(AppSimpleFragment.Layout.PROGRESS))

        presenter = MainPresenter(this)
//        presenter.requestAlbums()
    }

    override fun onDestroy() {
        presenter.destroy()

        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()

        if (slideShowFiles.size > 1) presentSlideShow()
    }

    override fun onStop() {
        super.onStop()

        handler.removeCallbacksAndMessages(null)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
        when (item?.itemId) {
            R.id.action_menu -> {
                logI("Menu action is selected.")
                true
            }
            R.id.action_license -> {
                logI("License action is selected.")
                presenter.requestShowLicenses()
                true
            }
            R.id.action_sign_out -> {
                logI("Sign out action is selected.")
                presenter.requestSignOut()
                true
            }
            else -> {
                logE("No actions are found.")
                super.onOptionsItemSelected(item)
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        presenter.evaluateActivityResult(requestCode, resultCode, data)
    }

    override fun getActivity(): Activity = this

    override fun requestUpdatePhotosResult(request: UpdatePhotosRequest) {
        slideShowFiles = files
        handler.removeCallbacksAndMessages(null)

        if (slideShowFiles.isEmpty()) {
            logE("Files for slide show is empty.")
            return
        }
        if (slideShowFiles.size == 1) {
            logE("Present one image.")
            presentImage(slideShowFiles[0])
            return
        }
        index = 0
        presentSlideShow()
    }

    override fun requestSignOutResult(result: ApiResult) {
        if (result == ApiResult.SUCCEEDED) {
            finish();
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
     * 画像を表示する.
     * @param filePath 画像ファイルパス
     */
    private fun presentImage(filePath: String) {
        for (fragment in fragmentManager.fragments) {
            if (fragment is SlideShowFragment) {
                logI("Update image to fragment. FilePath: $filePath")
                fragment.setImageView(filePath)
                return
            }
        }
        logI("Set image to new fragment. FilePath: $filePath")
        replaceFragment(SlideShowFragment.newInstance(filePath))
    }

    /**
     * ファイルリストからインデックスに該当するファイルをスライドショーに表示する.
     */
    private fun presentSlideShow() {
        logI("Present image. Index: $index")
        presentImage(slideShowFiles[index])

        handler.postDelayed({
            index = if (index + 1 < slideShowFiles.size) index + 1 else 0
            presentSlideShow()
        }, PRESENT_TIME_MILLISECS)
    }
}