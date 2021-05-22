package org.fog_rock.photo_slideshow.app.module.ui.extension

import androidx.fragment.app.Fragment
import org.fog_rock.photo_slideshow.core.extension.downCast

/**
 * フラグメントコールバック
 */
interface FragmentCallback {
    /**
     * フラグメントのビュー生成.
     * onCreateView のタイミングで呼び出すこと.
     * @see Fragment.onCreateView
     */
    fun onCreateViewFragment(className: String)
}

inline fun <reified CallbackT: FragmentCallback> Fragment.getActivityCallback(): CallbackT? =
    requireActivity().downCast()
