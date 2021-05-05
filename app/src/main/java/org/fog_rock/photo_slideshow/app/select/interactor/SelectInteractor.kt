package org.fog_rock.photo_slideshow.app.select.interactor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.photos.types.proto.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.fog_rock.photo_slideshow.app.module.lib.AppDatabase
import org.fog_rock.photo_slideshow.app.module.lib.GoogleWebApis
import org.fog_rock.photo_slideshow.app.select.contract.SelectContract
import org.fog_rock.photo_slideshow.core.extension.logE
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.viper.ViperContract
import java.util.concurrent.CancellationException

class SelectInteractor(
    private val appDatabase: AppDatabase,
    private val googleWebApis: GoogleWebApis
) : ViewModel(), SelectContract.Interactor {

    private var callback: SelectContract.InteractorCallback? = null

    override fun create(callback: ViperContract.InteractorCallback) {
        if (callback is SelectContract.InteractorCallback) {
            this.callback = callback
            createLoad()
        } else {
            throw IllegalArgumentException("SelectContract.InteractorCallback should be set.")
        }
    }

    override fun destroy() {
        viewModelScope.cancel(CancellationException("Destroy method is called."))
        callback = null
    }

    private fun createLoad() {
        viewModelScope.launch(Dispatchers.Default) {
            logI("initLoad: Start coroutine.")
            val albums = loadSharedAlbums()
            withContext(Dispatchers.Main) {
                callback?.createLoadResult(albums)
            }
            logI("initLoad: End coroutine.")
        }
    }

    private suspend fun loadSharedAlbums(): List<Album> {
        val emailAddress = googleWebApis.getSignedInEmailAddress()
        val userInfo = appDatabase.findUserInfoByEmailAddress(emailAddress) ?: run {
            logE("Not found UserInfo from database.")
            return emptyList()
        }
        val result = googleWebApis.requestSharedAlbums()
        if (result.tokenInfo.afterUpdated(userInfo.tokenInfo())) {
            // トークン情報が更新されていたらDB側も更新する.
            logI("TokenInfo is updated. Also need to update database.")
            appDatabase.updateUserInfo(emailAddress, result.tokenInfo)
        }
        return result.photosResults
    }
}