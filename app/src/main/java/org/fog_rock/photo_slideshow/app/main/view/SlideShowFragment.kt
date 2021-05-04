package org.fog_rock.photo_slideshow.app.main.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.fog_rock.photo_slideshow.core.extension.logE
import org.fog_rock.photo_slideshow.databinding.FragmentSlideShowBinding

class SlideShowFragment : Fragment() {

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
            logE("Not found arguments.")
            Bundle()
        }
    }

    private var _binding: FragmentSlideShowBinding? = null
    private val binding get() = _binding!!

    private var bitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSlideShowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val filePath = args.getString(ARGS_FILE_PATH, "")
        setImageView(filePath)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
        recycleBitmap(bitmap)
    }

    /**
     * イメージ画像を設置する.
     * @param filePath ファイルパス
     * @return 画像設置に成功したら true, 失敗したら false
     */
    fun setImageView(filePath: String): Boolean {
        if (filePath.isEmpty()) {
            logE("Failed to get initialized image file.")
            return false
        }
        val newBitmap = BitmapFactory.decodeFile(filePath) ?: run {
            logE("Failed to convert bitmap.")
            return false
        }
        binding.imageView.setImageBitmap(newBitmap)
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
