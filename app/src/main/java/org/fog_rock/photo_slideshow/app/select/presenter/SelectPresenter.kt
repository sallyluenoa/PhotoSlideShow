package org.fog_rock.photo_slideshow.app.select.presenter

import com.google.photos.types.proto.Album
import org.fog_rock.photo_slideshow.app.select.contract.SelectContract
import org.fog_rock.photo_slideshow.core.extension.downCast
import org.fog_rock.photo_slideshow.core.viper.ViperContract

class SelectPresenter(
    private var interactor: SelectContract.Interactor?
) : SelectContract.Presenter, SelectContract.InteractorCallback {

    private var callback: SelectContract.PresenterCallback? = null

    override fun create(callback: ViperContract.PresenterCallback) {
        this.callback = callback.downCast()
            ?: throw IllegalArgumentException("SelectContract.PresenterCallback should be set.")
        interactor?.create(this)
    }

    override fun destroy() {
        interactor?.destroy()
        interactor = null
        callback = null
    }

    override fun createLoadResult(albums: List<Album>) {
        callback?.createLoadResult(albums)
    }
}