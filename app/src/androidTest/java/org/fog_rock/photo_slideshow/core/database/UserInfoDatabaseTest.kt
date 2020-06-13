package org.fog_rock.photo_slideshow.core.database

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.fog_rock.photo_slideshow.core.database.impl.UserInfoDatabaseImpl
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UserInfoDatabaseTest {

    companion object {
        private const val EMAIL = "test@example.com"

        private const val ACCESS_TOKEN1 = "access_token1"
        private const val REFRESH_TOKEN1 = "refresh_token1"
        private const val EXPIRED_TIME_MILLIS1 = 1001L

        private const val ACCESS_TOKEN2 = "access_token2"
        private const val REFRESH_TOKEN2 = "refresh_token2"
        private const val EXPIRED_TIME_MILLIS2 = 1010L

        private val TAG = UserInfoDatabaseTest::class.java.simpleName
    }

    private val context = InstrumentationRegistry.getInstrumentation().context

    private val tokenInfo1 = TokenInfo(ACCESS_TOKEN1, REFRESH_TOKEN1, EXPIRED_TIME_MILLIS1)
    private val tokenInfo2 = TokenInfo(ACCESS_TOKEN2, REFRESH_TOKEN2, EXPIRED_TIME_MILLIS2)

    private val database: UserInfoDatabase = UserInfoDatabaseImpl(context)

    @Before
    fun configDatabaseBefore() = showDatabase()

    @After
    fun configDatabaseAfter() = showDatabase()

    @Test
    fun update() {
        val userInfo1 = runBlocking {
            database.update(EMAIL, tokenInfo1)
            database.find(EMAIL)
        }
        assertNotNull(userInfo1)
        assertEquals(EMAIL, userInfo1?.emailAddress)
        assertEquals(ACCESS_TOKEN1, userInfo1?.accessToken)
        assertEquals(REFRESH_TOKEN1, userInfo1?.refreshToken)
        assertEquals(EXPIRED_TIME_MILLIS1, userInfo1?.accessToken)

        val userInfo2 = runBlocking {
            database.update(EMAIL, tokenInfo2)
            database.find(EMAIL)
        }
        assertNotNull(userInfo2)
        assertEquals(EMAIL, userInfo2?.emailAddress)
        assertEquals(ACCESS_TOKEN1, userInfo2?.accessToken)
        assertEquals(REFRESH_TOKEN1, userInfo2?.refreshToken)
        assertEquals(EXPIRED_TIME_MILLIS1, userInfo2?.accessToken)
    }

    @Test
    fun delete() {
        val userInfo = runBlocking {
            database.delete(EMAIL)
            database.find(EMAIL)
        }
        assertNull(userInfo)
    }

    private fun showDatabase() {
        val usersInfo = runBlocking {
            database.getAll()
        }
        Log.i(TAG, "id\temailAddress\taccessToken\trefreshToken\texpAccessToken\tupdateDate")
        usersInfo.forEach {
            Log.i(TAG, "${it.id}\t${it.emailAddress}\t" +
                    "${it.accessToken}\t${it.refreshToken}\t" +
                    "${it.expiredAccessTokenTimeMillis}\t${it.updateDateTimeMillis}")
        }
    }
}