package org.fog_rock.photo_slideshow.app.main.presenter

import org.fog_rock.photo_slideshow.app.main.contract.MainContract
import org.fog_rock.photo_slideshow.app.main.router.MainRouter

class MainPresenter(private val callback: MainContract.PresenterCallback) : MainContract.Presenter {

    private val router: MainContract.Router = MainRouter()

    override fun destroy() {
    }

    override fun showSelectActivity() {
        router.startSelectActivity(activity(), 1000)
    }

    private fun activity() = callback.getActivity()

}