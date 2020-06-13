package org.fog_rock.photo_slideshow.core.database

import org.fog_rock.photo_slideshow.core.database.entity.UserInfo
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo

/**
 * ユーザー情報に関連するデータベース.
 * https://developer.android.com/training/data-storage/room?hl=ja
 */
interface UserInfoDatabase {

    /**
     * ユーザー情報がデータベース上に登録されている場合は更新、ない場合は新規追加する.
     * コルーチン内で呼び出すこと.
     * @param email ユーザーアカウント(Email)
     * @param tokenInfo トークン情報
     * @return 追加更新した場合はtrue、情報不足により失敗した場合はfalse
     */
    suspend fun update(email: String, tokenInfo: TokenInfo): Boolean

    /**
     * ユーザー情報を削除する.
     * コルーチン内で呼び出すこと.
     * @param email ユーザーアカウント(Email)
     */
    suspend fun delete(email: String)

    /**
     * ユーザー情報を検索する.
     * コルーチン内で呼び出すこと.
     * @param email ユーザーアカウント(Email)
     * @return 検索にヒットしたユーザー情報、見つからなかった場合はNULL
     */
    suspend fun find(email: String): UserInfo?

    /**
     * 登録されている全ユーザー情報を取得する.
     * コルーチン内で呼び出すこと.
     * @return 全ユーザー情報
     */
    suspend fun getAll(): List<UserInfo>
}