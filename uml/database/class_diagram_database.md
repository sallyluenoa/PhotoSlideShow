@startuml
namespace core.database #FFEEFF {

  namespace entity {
    interface BaseEntity {
      + id: Long
      + createTimeMillis: Long
      + updateTimeMillis: Long
    }
    class UserInfo << (D, sandybrown) >> {
      + emailAdress: String
      - tokenInfo: String
      + updatePhotosTimeMillis: Long
      + tokenInfo(): TokenInfo
      + copy(tokenInfo: TokenInfo): UserInfo
      + updatePhotos(): UserInfo
      + isNeededUpdatePhotos(intervalTimeMillis: Long): Boolean
      + isAvailableAccessToken(intervalTimeMillis: Long): Boolean
    }
    class SelectedAlbum << (D, sandybrown) >> {
      + userInfoId: Long
      + albumId: String
      - album: String
      + album(): Album
      + copy(album: Album): SelectedAlbum
    }
    class DisplayedPhoto << (D, sandybrown) >> {
      + selectedAlbumId: Long
      + mediaItemId: String
      - mediaItem: String
      + outputPath: String
      + isMyFavorite: Boolean
      + mediaItem(): MediaItem
      + copy(mediaItem: MediaItem, outputPath: String): DisplayedPhoto
    }
    class UserInfoData << (D, sandybrown) >>
    class SelectedData << (D, sandybrown) >>

    UserInfo ..|> BaseEntity
    SelectedAlbum ..|> BaseEntity
    DisplayedPhoto ..|> BaseEntity

    UserInfoData "1" *-- "1" UserInfo: + userInfo
    UserInfoData "1" *-- "*" SelectedData: + dataList 
    SelectedData "1" *-- "1" SelectedAlbum: + selectedAlbum
    SelectedData "1" *-- "*" DisplayedPhoto: + displayedPhotos
  }

  namespace dao {
    interface BaseDao<EntityT> {
      + <B>[suspend]</B> insert(entity: EntityT): Long
      + <B>[suspend]</B> insert(entities: List<EntityT>): List<Long>
      + <B>[suspend]</B> update(entity: EntityT)
      + <B>[suspend]</B> update(entities: List<EntityT>)
      + <B>[suspend]</B> delete(entity: EntityT)
      + <B>[suspend]</B> delete(entities: List<EntityT>)
      + <B>[suspend]</B> findById(id: long): EntityT?
    }
    interface UserInfoDao {
      + <B>[suspend]</B> findByEmailAddress(emailAddress: String): UserInfo?
      + <B>[suspend]</B> findUserInfoDataById(id: Long): UserInfoData?
      + <B>[suspend]</B> findUserInfoDataByEmailAddress(emailAddress: String): UserInfoData?
    }
    interface SelectedAlbumDao {
      + <B>[suspend]</B> findByUniqueKeys(userInfoId: Long, albumId: String): SelectedAlbum?
      + <B>[suspend]</B> findSelectedDataById(id: Long): SelectedData?
      + <B>[suspend]</B> findSelectedDataByUniqueKeys(userInfoId: Long, albumId: String): SelectedData?
    }
    interface DisplayedPhotoDao {
      + <B>[suspend]</B> findByUniqueKeys(selectedAlbumId: Long, mediaItemId: String): DisplayedPhoto?
    }

    UserInfoDao ..|> BaseDao
    SelectedAlbumDao ..|> BaseDao
    DisplayedPhotoDao ..|> BaseDao

    UserInfoDao -[hidden]-> SelectedAlbumDao
    SelectedAlbumDao -[hidden]-> DisplayedPhotoDao
  }

  namespace room {
    class SingletonRoomObject << (O, gold) Singleton >> {
      + setup(context: Context)
      + userInfoDao(): UserInfoDao
      + selectedAlbumDao(): SelectedAlbumDao
      + displayedPhotoDao(): DisplayedPhotoDao
    }
    abstract class SingletonRoomDatabase {
      + {abstract} userInfoDao(): UserInfoDao
      + {abstract} selectedAlbumDao(): SelectedAlbumDao
      + {abstract} displayedPhotoDao(): DisplayedPhotoDao
    }

    SingletonRoomObject "1" *-down- "1" SingletonRoomDatabase: - database
  }
}

namespace app.module #FFFFEE {

  namespace entity {
    class PhotoInfo << (D, sandybrown) >> {
      + album: Album
    }
    class MediaDetail << (D, sandybrown) >> {
      + mediaItem: MediaItem
      + outputPath: String
    }

    PhotoInfo "1" *-right- "*" MediaDetail: + mediaDetails
  } 

  class AppDatabase {
    + <B>[suspend]</B> updateUserInfo(emailAddress: String, tokenInfo: TokenInfo)
    + <B>[suspend]</B> deleteUserInfo(id: Long)
    + <B>[suspend]</B> replaceUserInfoData(userInfo: UserInfo, photosInfo: List<PhotoInfo>)
    + <B>[suspend]</B> findUserInfoByEmailAddress(emailAddress: String): UserInfo?
    + <B>[suspend]</B> findUserInfoDataById(id: Long): UserInfoData?
    + <B>[suspend]</B> findUserInfoDataByEmailAddress(emailAddress: String): UserInfoData?
    - userInfoDao(): UserInfoDao
    - selectedAlbumDao(): SelectedAlbumDao
    - displayedPhotoDao(): DisplayedPhotoDao
  }
}

core.database.room.SingletonRoomDatabase o-right- core.database.dao.UserInfoDao
core.database.room.SingletonRoomDatabase o-right- core.database.dao.SelectedAlbumDao
core.database.room.SingletonRoomDatabase o-right- core.database.dao.DisplayedPhotoDao

app.module.AppDatabase -- core.database.room.SingletonRoomObject
@enduml