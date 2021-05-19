package org.fog_rock.photo_slideshow.app.main.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import org.fog_rock.photo_slideshow.app.main.contract.MainContract
import org.fog_rock.photo_slideshow.app.main.entity.UpdatePhotosRequest
import org.fog_rock.photo_slideshow.app.main.interactor.MainInteractor
import org.fog_rock.photo_slideshow.app.main.presenter.MainPresenter
import org.fog_rock.photo_slideshow.app.main.router.MainRouter
import org.fog_rock.photo_slideshow.app.module.lib.impl.AppDatabaseImpl
import org.fog_rock.photo_slideshow.app.module.lib.impl.AppSettingsImpl
import org.fog_rock.photo_slideshow.app.module.lib.impl.GoogleWebApisImpl
import org.fog_rock.photo_slideshow.app.module.lib.impl.PhotosDownloaderImpl
import org.fog_rock.photo_slideshow.app.module.ui.AppSimpleFragment
import org.fog_rock.photo_slideshow.app.module.ui.replaceFragment
import org.fog_rock.photo_slideshow.core.database.entity.DisplayedPhoto
import org.fog_rock.photo_slideshow.core.extension.ONE_SECOND_MILLIS
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.extension.logW
import org.fog_rock.photo_slideshow.core.file.impl.FileDownloaderImpl
import org.fog_rock.photo_slideshow.core.math.impl.SizeCalculatorImpl
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleOAuth2ApiImpl
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleSignInApiImpl
import org.fog_rock.photo_slideshow.core.webapi.impl.PhotosLibraryApiImpl
import org.fog_rock.photo_slideshow.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), MainContract.PresenterCallback {

    companion object {
        private const val ASPECT_WIDTH = 500L
        private const val ASPECT_HEIGHT = 1000L
    }

    private lateinit var binding: ActivityMainBinding

    private var presenter: MainContract.Presenter? = null

    private var displayedPhotos = emptyList<DisplayedPhoto>()
    private var displayedIndex = 0
    private var timeIntervalSecs = 0
    private var isRequestingUpdatePhotos = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(AppSimpleFragment.newInstance(AppSimpleFragment.Layout.PROGRESS))

        binding.menuButton.setOnClickListener {
            presenter?.requestShowMenu()
        }

        presenter = MainPresenter(
            MainInteractor(
                AppSettingsImpl(this),
                AppDatabaseImpl(),
                PhotosDownloaderImpl(FileDownloaderImpl(), SizeCalculatorImpl(), ASPECT_WIDTH, ASPECT_HEIGHT),
                GoogleWebApisImpl(this, GoogleSignInApiImpl(), GoogleOAuth2ApiImpl(), PhotosLibraryApiImpl())
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        presenter?.evaluateActivityResult(requestCode, resultCode, data)
    }

    override fun getActivity(): Activity = this

    override fun requestLoadDisplayedPhotosResult(displayedPhotos: List<DisplayedPhoto>, timeIntervalSecs: Int) {
        logI("requestLoadDisplayedPhotosResult() " +
                "displayedPhotosSize: ${displayedPhotos.size}, timeIntervalSecs: $timeIntervalSecs")
        updateDisplayedPhotos(displayedPhotos, timeIntervalSecs)
    }

    override fun requestUpdateDisplayedPhotosResult(request: UpdatePhotosRequest) {
        logI("requestUpdateDisplayedPhotosResult: $request")
        isRequestingUpdatePhotos = false
        if (request == UpdatePhotosRequest.COMPLETED) {
            requestLoadDisplayedPhotos()
        }
    }

    /**
     * 画像を表示する.
     * @param filePath 画像ファイルパス
     */
    private fun presentImage(filePath: String) {
        for (fragment in supportFragmentManager.fragments) {
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
                        delay(timeIntervalSecs * ONE_SECOND_MILLIS)
                    } else {
                        // 一秒間待つ.
                        logI("Wait 1 sec...")
                        delay(ONE_SECOND_MILLIS)
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
    private fun updateDisplayedPhotos(displayedPhotos: List<DisplayedPhoto>, timeIntervalSecs: Int) {
        this.timeIntervalSecs = timeIntervalSecs
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