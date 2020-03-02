package org.fog_rock.photo_slideshow.app.main.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.main.contract.MainContract
import org.fog_rock.photo_slideshow.app.main.presenter.MainPresenter

class MainActivity : AppCompatActivity(), MainContract.PresenterCallback {

    private val TAG = MainActivity::class.java.simpleName

    private val PRESENT_TIME_MILLISECS = 5000L

    private val fragmentManager = supportFragmentManager

    private val handler = Handler()

    private lateinit var presenter: MainContract.Presenter

    private var slideShowFiles = listOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        replaceFragment(ProgressFragment.newInstance())

        presenter = MainPresenter(this)
        presenter.requestAlbums()
    }

    override fun onDestroy() {
        presenter.destroy()
        handler.removeCallbacksAndMessages(null)

        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        presenter.evaluateActivityResult(requestCode, resultCode, data)
    }

    override fun getActivity(): Activity = this

    override fun requestSlideShow(files: List<String>) {
        slideShowFiles = files
        handler.removeCallbacksAndMessages(null)

        if (slideShowFiles.isEmpty()) {
            Log.e(TAG, "Files for slide show is empty.")
            return
        }
        if (slideShowFiles.size == 1) {
            Log.i(TAG, "Present one image.")
            presentSlideShow(slideShowFiles[0])
            return
        }
        presentSlideShow(0)
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
     * ファイルをスライドショーに表示する.
     * @param filePath ファイルパス
     */
    private fun presentSlideShow(filePath: String) {
        for (fragment in fragmentManager.fragments) {
            if (fragment is SlideShowFragment) {
                Log.i(TAG, "Update image to fragment. FilePath: $filePath")
                fragment.setImageView(filePath)
                return
            }
        }
        Log.i(TAG, "Set image to new fragment. FilePath: $filePath")
        replaceFragment(SlideShowFragment.newInstance(filePath))
    }

    /**
     * ファイルリストからインデックスに該当するファイルをスライドショーに表示する.
     * @param index インデックス
     */
    private fun presentSlideShow(index: Int) {
        Log.i(TAG, "Present image. Index: $index")
        presentSlideShow(slideShowFiles[index])

        handler.postDelayed({
            val nextIndex = if (index + 1 < slideShowFiles.size) index + 1 else 0
            presentSlideShow(nextIndex)
        }, PRESENT_TIME_MILLISECS)
    }
}