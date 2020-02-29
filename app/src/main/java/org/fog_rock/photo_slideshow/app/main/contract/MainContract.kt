package org.fog_rock.photo_slideshow.app.main.contract

import android.app.Activity
import org.fog_rock.photo_slideshow.core.viper.ViperContract

class MainContract {

    interface Presenter : ViperContract.Presenter {
        fun showSelectActivity()
    }

    interface PresenterCallback : ViperContract.PresenterCallback {

    }

    interface Interactor : ViperContract.Interactor {

    }

    interface InteractorCallback : ViperContract.InteractorCallback {

    }

    interface Router : ViperContract.Router {
        fun startSelectActivity(activity: Activity, requestCode: Int)
    }
}