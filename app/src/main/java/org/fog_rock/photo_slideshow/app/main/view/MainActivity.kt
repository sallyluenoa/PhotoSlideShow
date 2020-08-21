package org.fog_rock.photo_slideshow.app.main.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.main.contract.MainContract
import org.fog_rock.photo_slideshow.app.main.entity.UpdatePhotosRequest
import org.fog_rock.photo_slideshow.app.main.interactor.MainInteractor
import org.fog_rock.photo_slideshow.app.main.presenter.MainPresenter
import org.fog_rock.photo_slideshow.app.main.router.MainRouter
import org.fog_rock.photo_slideshow.app.module.AppDatabase
import org.fog_rock.photo_slideshow.app.module.AppSimpleFragment
import org.fog_rock.photo_slideshow.app.module.GoogleWebApis
import org.fog_rock.photo_slideshow.core.database.entity.DisplayedPhoto
import org.fog_rock.photo_slideshow.core.extension.logE
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.file.impl.FileDownloaderImpl
import org.fog_rock.photo_slideshow.core.file.impl.PhotosDownloaderImpl
import org.fog_rock.photo_slideshow.core.webapi.entity.ApiResult
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleOAuth2ApiImpl
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleSignInApiImpl
import org.fog_rock.photo_slideshow.core.webapi.impl.PhotosLibraryApiImpl

class MainActivity : AppCompatActivity(), MainContract.PresenterCallback {

    companion object {
        private const val PRESENT_TIME_MILLISECS = 5000L

        private const val ASPECT_WIDTH = 500L
        private const val ASPECT_HEIGHT = 1000L
    }

    private val fragmentManager = supportFragmentManager

    private var presenter: MainContract.Presenter? = null

    private var displayedPhotos = emptyList<DisplayedPhoto>()
    private var displayedIndex = 0

    private var isForeground = false
    private var isUpdateRequested = false
    private var isRunningSlideShow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        replaceFragment(AppSimpleFragment.newInstance(AppSimpleFragment.Layout.PROGRESS))

        presenter = MainPresenter(
            MainInteractor(this, AppDatabase(),
                PhotosDownloaderImpl(FileDownloaderImpl(), ASPECT_WIDTH, ASPECT_HEIGHT),
                GoogleWebApis(GoogleSignInApiImpl(this), GoogleOAuth2ApiImpl(), PhotosLibraryApiImpl())),
            MainRouter()
        )
        presenter?.create(this)

        presenter?.requestLoadDisplayedPhotos()
    }

    override fun onDestroy() {
        presenter?.destroy()
        presenter = null

        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()

        isForeground = true
        if (displayedPhotos.isNotEmpty()) presentSlideShow()
    }

    override fun onPause() {
        isForeground = false
        lifecycleScope.cancel(CancellationException("MainActivity will be background."))

        super.onPause()
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
                presenter?.requestShowLicenses()
                true
            }
            R.id.action_sign_out -> {
                logI("Sign out action is selected.")
                presenter?.requestSignOut()
                true
            }
            else -> {
                logE("No actions are found.")
                super.onOptionsItemSelected(item)
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        presenter?.evaluateActivityResult(requestCode, resultCode, data)
    }

    override fun getActivity(): Activity = this

    override fun requestLoadDisplayedPhotosResult(displayedPhotos: List<DisplayedPhoto>) {
        isUpdateRequested = false
        if (displayedPhotos.isEmpty()) {
            presenter?.requestUpdateDisplayedPhotos()
        } else {
            this.displayedPhotos = displayedPhotos
            this.displayedIndex = 0
            presentSlideShow()
        }
    }

    override fun requestUpdateDisplayedPhotosResult(request: UpdatePhotosRequest) {
        if (request == UpdatePhotosRequest.COMPLETED) {
            if (isRunningSlideShow) {
                isUpdateRequested = true
            } else {
                presenter?.requestLoadDisplayedPhotos()
            }
        }
    }

    override fun requestSignOutResult(result: ApiResult) {
        if (result == ApiResult.SUCCEEDED) {
            finish()
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
        if (!isForeground) {
            logI("MainActivity is background now. Skipped to present SlideShow.")
            return
        }

        lifecycleScope.launch(Dispatchers.Main) {
            try {
                isRunningSlideShow = true
                while (!isUpdateRequested) {
                    presentImage(displayedPhotos[displayedIndex].outputPath)
                    delay(PRESENT_TIME_MILLISECS)
                    displayedIndex =
                        if (displayedIndex + 1 < displayedPhotos.size) displayedIndex + 1 else 0
                    if (displayedIndex == 0) presenter?.requestUpdateDisplayedPhotos()
                }
                isRunningSlideShow = false
                presenter?.requestLoadDisplayedPhotos()
            } catch (e: CancellationException) {
                logI("SlideShow loop was canceled. Reason: $e")
            }
        }
    }
}