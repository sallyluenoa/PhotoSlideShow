package org.fog_rock.photo_slideshow.core.database.impl

import android.content.Context
import androidx.room.Room
import org.fog_rock.photo_slideshow.core.database.UserInfoDatabase
import org.fog_rock.photo_slideshow.core.database.entity.UserInfo
import org.fog_rock.photo_slideshow.core.extension.logE
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo

class UserInfoDatabaseImpl(context: Context): UserInfoDatabase {

    private val database = Room.databaseBuilder(
        context,
        UserInfoRoomDatabase::class.java,
        "userinfo_database"
    ).build()

    override suspend fun update(email: String, tokenInfo: TokenInfo): Boolean {
        val userInfo = find(email) ?: run {
            // データが見つからない. 新規追加.
            return try {
                database.dao().insert(UserInfo.newUserInfo(email, tokenInfo))
                true
            } catch (e: java.lang.IllegalArgumentException) {
                logE("Failed to create UserInfo.")
                e.printStackTrace()
                false
            }
        }
        // データが見つかった. 更新.
        database.dao().update(UserInfo.mergeUserInfo(userInfo, tokenInfo))
        return true
    }

    override suspend fun delete(email: String) {
        val dao = database.dao()
        val usersInfo = dao.find(email)
        usersInfo.forEach { dao.delete(it) }
    }

    override suspend fun find(email: String): UserInfo? {
        val usersInfo = database.dao().find(email)
        return if (!usersInfo.isNullOrEmpty()) usersInfo[0] else null
    }

    override suspend fun getAll(): List<UserInfo> = database.dao().getAll()
}