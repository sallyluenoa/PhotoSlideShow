package org.fog_rock.photo_slideshow.app.module

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.fog_rock.photo_slideshow.R

class AppSimpleFragment : Fragment() {

    private val TAG = AppSimpleFragment::class.java.simpleName

    companion object {

        private const val ARGS_LAYOUT = "layout"

        fun newInstance(layout: Layout): Fragment {
            val args = Bundle().apply {
                putSerializable(ARGS_LAYOUT, layout)
            }
            return AppSimpleFragment().apply {
                arguments = args
            }
        }
    }

    enum class Layout(val resId: Int) {

        EMPTY(R.layout.fragment_empty),

        LOGO(R.layout.fragment_logo),

        PROGRESS(R.layout.fragment_progress),
    }

    private val args: Bundle by lazy {
        arguments ?: run {
            Log.e(TAG, "Not found arguments.")
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
