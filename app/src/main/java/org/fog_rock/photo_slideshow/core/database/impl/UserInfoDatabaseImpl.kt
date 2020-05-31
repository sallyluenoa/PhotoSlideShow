package org.fog_rock.photo_slideshow.core.database.impl

import android.content.Context
import android.util.Log
import androidx.room.Room
import org.fog_rock.photo_slideshow.core.database.UserInfoDatabase
import org.fog_rock.photo_slideshow.core.database.entity.UserInfo
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo

class UserInfoDatabaseImpl(context: Context): UserInfoDatabase {

    private val TAG = UserInfoDatabaseImpl::class.java.simpleName

    private val database = Room.databaseBuilder(
        context,
        UserInfoRoomDatabase::class.java,
        "userinfo_database"
    ).build()

    override fun toString(): String = getAll().toString()

    override fun update(email: String, tokenInfo: TokenInfo): Boolean {
        val userInfo = find(email) ?: run {
            // データが見つからない. 新規追加.
            return try {
                database.dao().insert(UserInfo.newUserInfo(email, tokenInfo))
                true
            } catch (e: java.lang.IllegalArgumentException) {
                Log.e(TAG, "Failed to create UserInfo.")
                e.printStackTrace()
                false
            }
        }
        // データが見つかった. 更新.
        database.dao().update(UserInfo.mergeUserInfo(userInfo, tokenInfo))
        return true
    }

    override fun delete(email: String) {
        val dao = database.dao()
        val usersInfo = dao.find(email)
        usersInfo.forEach { dao.delete(it) }
    }

    override fun find(email: String): UserInfo? {
        val usersInfo = database.dao().find(email)
        return if (!usersInfo.isNullOrEmpty()) usersInfo[0] else null
    }

    override fun getAll(): List<UserInfo> = database.dao().getAll()
}