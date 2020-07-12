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

    override suspend fun delete(email: String) {
        val dao = dao()
//        val usersInfo = dao.find(email)
//        usersInfo.forEach { dao.delete(it) }
    }

    override suspend fun find(email: String): UserInfo? {
        return null
 //       val usersInfo = database.dao().find(email)
//        return if (!usersInfo.isNullOrEmpty()) usersInfo[0] else null
    }

    override suspend fun findWithSelectedAlbums(email: String): UserInfoWithSelectedAlbums? =
        dao().findWithSelectedAlbums(email)

    private fun dao(): UserInfoDao = SingletonRoomObject.userInfoDao()
}