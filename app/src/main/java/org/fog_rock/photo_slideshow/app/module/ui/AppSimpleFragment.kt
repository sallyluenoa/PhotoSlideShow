package org.fog_rock.photo_slideshow.app.module.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.core.extension.logE

/**
 * 定められた画面レイアウトのフラグメントを表示するためのクラス.
 * レイアウトにはボタンイベントや処理がない.
 */
class AppSimpleFragment : Fragment() {

    companion object {

        private const val ARGS_LAYOUT = "layout"

        /**
         * 新規インスタンス生成.
         */
        fun newInstance(layout: Layout): Fragment = AppSimpleFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARGS_LAYOUT, layout)
            }
        }
    }

    /**
     * 画面レイアウト種別.
     */
    enum class Layout(@LayoutRes val resId: Int) {
        /**
         * 真っ白なレイアウト
         */
        EMPTY(R.layout.fragment_empty),

        /**
         * アプリロゴが表示されたレイアウト
         */
        LOGO(R.layout.fragment_logo),

        /**
         * プログレスが表示されたレイアウト
         */
        PROGRESS(R.layout.fragment_progress),
    }

    private val args: Bundle by lazy {
        arguments ?: run {
            logE("Not found arguments.")
            Bundle()
        }
    }

    private val layout: Layout by lazy {
        args.getSerializable(ARGS_LAYOUT) as Layout
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layout.resId, container, false)
}
