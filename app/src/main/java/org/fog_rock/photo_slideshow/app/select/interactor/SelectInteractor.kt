package org.fog_rock.photo_slideshow.app.select.interactor

import com.google.photos.types.proto.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.fog_rock.photo_slideshow.app.module.AppDatabase
import org.fog_rock.photo_slideshow.app.module.GoogleWebApis
import org.fog_rock.photo_slideshow.app.select.contract.SelectContract
import org.fog_rock.photo_slideshow.core.viper.ViperContract

class SelectInteractor(
    private val appDatabase: AppDatabase,
    private val googleWebApis: GoogleWebApis
) : SelectContract.Interactor {

    private var callback: SelectContract.InteractorCallback? = null

    override fun create(callback: ViperContract.InteractorCallback) {
        if (callback is SelectContract.InteractorCallback) {
            this.callback = callback
        } else {
            IllegalArgumentException("SelectContract.InteractorCallback should be set.")
        }
    }

    override fun destroy() {
        callback = null
    }

    override fun requestLoadSharedAlbums() {
        GlobalScope.launch(Dispatchers.Default) {
            val emailAddress = googleWebApis.getSignedInEmailAddress()
            val userInfo = appDatabase.findUserInfoByEmailAddress(emailAddress) ?: run {
                requestLoadSharedAlbumsResult(emptyList())
                return@launch
            }
            val result = googleWebApis.requestSharedAlbums()
            if (result.tokenInfo.afterUpdated(userInfo.tokenInfo())) {
                // トークン情報が更新されていたらDB側も更新する.
                appDatabase.updateUserInfo(emailAddress, result.tokenInfo)
            }
            requestLoadSharedAlbumsResult(result.photosResults)
        }
    }

    private suspend fun requestLoadSharedAlbumsResult(albums: List<Album>) {
        withContext(Dispatchers.Main) {
            callback?.requestLoadSharedAlbumsResult(albums)
        }
    }
}