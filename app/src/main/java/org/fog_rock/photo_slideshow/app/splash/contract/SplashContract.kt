package org.fog_rock.photo_slideshow.app.splash.contract

import org.fog_rock.photo_slideshow.core.viper.BaseInteractor
import org.fog_rock.photo_slideshow.core.viper.BasePresenter
import org.fog_rock.photo_slideshow.core.viper.BaseRouter
import org.fog_rock.photo_slideshow.core.viper.BaseView

class SplashContract {

    interface View : BaseView
    interface Presenter : BasePresenter
    interface Interactor : BaseInteractor
    interface Router : BaseRouter
}