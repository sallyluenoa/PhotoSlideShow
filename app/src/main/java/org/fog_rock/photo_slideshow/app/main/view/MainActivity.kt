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
import org.fog_rock.photo_slideshow.app.module.lib.AppDatabase
import org.fog_rock.photo_slideshow.app.module.ui.AppSimpleFragment
import org.fog_rock.photo_slideshow.app.module.lib.GoogleWebApis
import org.fog_rock.photo_slideshow.core.database.entity.DisplayedPhoto
import org.fog_rock.photo_slideshow.core.extension.logE
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.extension.logW
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
    private var isRequestingUpdatePhotos = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        replaceFragment(
            AppSimpleFragment.newInstance(
                AppSimpleFragment.Layout.PROGRESS))

        presenter = MainPresenter(
            MainInteractor(this,
                AppDatabase(),
                PhotosDownloaderImpl(FileDownloaderImpl(), ASPECT_WIDTH, ASPECT_HEIGHT),
                GoogleWebApis(
                    GoogleSignInApiImpl(this),
                    GoogleOAuth2ApiImpl(),
                    PhotosLibraryApiImpl()
                )
            ),
            MainRouter()
        )
        presenter?.create(this)

        requestLoadDisplayedPhotos()
    }

    override fun onDestroy() {
        presenter?.destroy()
        presenter = null

        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()

        presentSlideShow()
    }

    override fun onPause() {
        lifecycleScope.coroutineContext.cancelChildren(CancellationException("MainActivity will be background."))

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
        updateDisplayedPhotos(displayedPhotos)
    }

    override fun requestUpdateDisplayedPhotosResult(request: UpdatePhotosRequest) {
        logI("requestUpdateDisplayedPhotosResult: $request")
        isRequestingUpdatePhotos = false
        if (request == UpdatePhotosRequest.COMPLETED) {
            requestLoadDisplayedPhotos()
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
        logI("presentSlideShow")

        lifecycleScope.launch(Dispatchers.Default) {
            logI("presentSlideShow: Start coroutine.")
            try {
                while (true) {
                    val displayedPhoto = getDisplayedPhoto()
                    if (displayedPhoto != null) {
                        // 指定された時間分写真を表示する.
                        logI("Display photo image.")
                        withContext(Dispatchers.Main) {
                            presentImage(displayedPhoto.outputPath)
                        }
                        delay(PRESENT_TIME_MILLISECS)
                    } else {
                        // 一秒間待つ.
                        logI("Wait 1 sec...")
                        delay(1000L)
                    }
                }
            } catch (e: CancellationException) {
                logW("SlideShow loop was canceled. Reason: $e")
            } finally {
                logI("SlideShow loop was ended.")
            }
        }
    }

    private fun requestLoadDisplayedPhotos() {
        logI("Request load displayed photos.")
        presenter?.requestLoadDisplayedPhotos()
    }

    private fun requestUpdateDisplayedPhotos() {
        logI("Request update displayed photos.")
        isRequestingUpdatePhotos = true
        presenter?.requestUpdateDisplayedPhotos()
    }

    @Synchronized
    private fun getDisplayedPhoto(): DisplayedPhoto? =
        if (displayedPhotos.isNotEmpty()) {
            logI("Get displayed photos. Index: $displayedIndex")
            if (displayedIndex == 0 && !isRequestingUpdatePhotos) {
                // index が 0 且つ更新処理中ではなかったら、更新チェックを行う.
                requestUpdateDisplayedPhotos()
            }
            val displayedPhoto = displayedPhotos[displayedIndex]
            displayedIndex = if (displayedIndex + 1 < displayedPhotos.size) displayedIndex + 1 else 0
            displayedPhoto
        } else {
            logI("There are no displayed photos.")
            null
        }

    @Synchronized
    private fun updateDisplayedPhotos(displayedPhotos: List<DisplayedPhoto>) {
        if (displayedPhotos.isNotEmpty()) {
            logI("Update new displayed photos.")
            this.displayedPhotos = displayedPhotos
            this.displayedIndex = 0
        } else {
            logI("Displayed photos is empty.")
            requestUpdateDisplayedPhotos()
        }
    }
}