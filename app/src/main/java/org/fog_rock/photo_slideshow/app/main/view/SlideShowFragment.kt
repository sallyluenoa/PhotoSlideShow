package org.fog_rock.photo_slideshow.app.main.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.transition.Slide
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_slide_show.*
import org.fog_rock.photo_slideshow.R
import java.io.File

class SlideShowFragment : Fragment() {

    private val TAG = SlideShowFragment::class.java.simpleName

    companion object {

        private const val ARGS_FILE_PATH = "file_path"

        fun newInstance(filePath: String): Fragment {
            val args = Bundle().apply {
                putSerializable(ARGS_FILE_PATH, filePath)
            }
            return SlideShowFragment().apply {
                arguments = args
            }
        }
    }

    private val args: Bundle by lazy {
        arguments ?: run {
            Log.e(TAG, "Not found arguments.")
            Bundle()
        }
    }

    private var bitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_slide_show, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val filePath = args.getString(ARGS_FILE_PATH, "")
        setImageView(filePath)
    }

    override fun onDestroy() {
        recycleBitmap(bitmap)

        super.onDestroy()
    }

    /**
     * イメージ画像を設置する.
     * @param filePath ファイルパス
     * @return 画像設置に成功したら true, 失敗したら false
     */
    fun setImageView(filePath: String): Boolean {
        if (filePath.isEmpty()) {
            Log.e(TAG, "Failed to get initialized image file.")
            return false
        }
        val newBitmap = BitmapFactory.decodeFile(filePath) ?: run {
            Log.e(TAG, "Failed to convert bitmap.")
            return false
        }
        imageView.setImageBitmap(newBitmap)
        recycleBitmap(bitmap)
        bitmap = newBitmap
        return true
    }

    /**
     * ビットマップオブジェクトをメモリ解放する.
     * @param oldBitmap ビットマップ
     */
    private fun recycleBitmap(oldBitmap: Bitmap?) {
        if (oldBitmap != null && oldBitmap.isRecycled) oldBitmap.recycle()
    }
}
