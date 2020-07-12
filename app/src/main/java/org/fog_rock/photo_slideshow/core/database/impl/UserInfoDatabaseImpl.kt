package org.fog_rock.photo_slideshow.core.database.impl

import org.fog_rock.photo_slideshow.core.database.UserInfoDatabase
import org.fog_rock.photo_slideshow.core.database.dao.UserInfoDao
import org.fog_rock.photo_slideshow.core.database.entity.UserInfo
import org.fog_rock.photo_slideshow.core.database.entity.UserInfoWithSelectedAlbums
import org.fog_rock.photo_slideshow.core.database.room.SingletonRoomObject
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo

class UserInfoDatabaseImpl: UserInfoDatabase {

    override suspend fun update(emailAddress: String, tokenInfo: TokenInfo) {
        val userInfo = dao().findByEmailAddress(emailAddress)
        if (userInfo != null) {
            dao().update(userInfo.copy(tokenInfo))
        } else {
            dao().insert(UserInfo(emailAddress, tokenInfo))
        }
    }

    override suspend fun delete(emailAddress: String) {
        val userInfo = dao().findByEmailAddress(emailAddress)
        if (userInfo != null) {
            dao().delete(userInfo)
        }
    }

    override suspend fun find(emailAddress: String): UserInfo? =
        dao().findByEmailAddress(emailAddress)

    override suspend fun findWithSelectedAlbums(emailAddress: String): UserInfoWithSelectedAlbums? =
        dao().findWithSelectedAlbums(emailAddress)

    private fun dao(): UserInfoDao = SingletonRoomObject.userInfoDao()
}