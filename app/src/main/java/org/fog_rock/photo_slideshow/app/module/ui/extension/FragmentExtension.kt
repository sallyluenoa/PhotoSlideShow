package org.fog_rock.photo_slideshow.app.module.ui.extension

import androidx.fragment.app.Fragment

inline fun <reified CallbackT: FragmentCallback> Fragment.getActivityCallback(): CallbackT? {
    val activity = requireActivity()
    return if (activity is CallbackT) activity else null
}

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