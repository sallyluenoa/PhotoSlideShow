package org.fog_rock.photo_slideshow.core.viper

import android.app.Activity

class ViperContract {

    interface Presenter {
        fun destroy()
    }

    interface PresenterCallback {
        fun getActivity(): Activity
    }

    interface Interactor {
        fun destroy()
    }

    interface InteractorCallback

    interface Router
}