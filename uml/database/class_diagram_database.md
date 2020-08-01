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
      + isMyFavorite: Boolean
      + mediaItem(): MediaItem
      + copy(mediaItem: MediaItem): DisplayedPhoto
    }
    class UserInfoWithSelectedAlbums << (D, sandybrown) >>
    class SelectedAlbumWithDisplayedPhotos << (D, sandybrown) >>

    UserInfo ..|> BaseEntity
    SelectedAlbum ..|> BaseEntity
    DisplayedPhoto ..|> BaseEntity

    UserInfoWithSelectedAlbums "1" *-- "1" UserInfo
    UserInfoWithSelectedAlbums "1" *-- "*" SelectedAlbum
    SelectedAlbumWithDisplayedPhotos "1" *-- "1" SelectedAlbum
    SelectedAlbumWithDisplayedPhotos "1" *-- "*" DisplayedPhoto
  }

  namespace dao {
    interface BaseDao<EntityT> {
      + insert(entity: EntityT)
      + insert(entities: List<EntityT>)
      + update(entity: EntityT)
      + update(entities: List<EntityT>)
      + delete(entity: EntityT)
      + delete(entities: List<EntityT>)
      + findById(id: long): EntityT?
    }
    interface UserInfoDao {
      + findByEmailAddress(emailAddress: String): UserInfo?
      + findWithSelectedAlbums(id: Long): UserInfoWithSelectedAlbums?
      + findWithSelectedAlbums(emailAddress: String): UserInfoWithSelectedAlbums?
    }
    interface SelectedAlbumDao {
      + findByUniqueKeys(userInfoId: Long, albumId: String): SelectedAlbum?
      + findWithDisplayedPhotos(id: Long): SelectedAlbumWithDisplayedPhotos?
    }
    interface DisplayedPhotoDao {
      + findByUniqueKeys(selectedAlbumId: Long, mediaItemId: String): DisplayedPhoto?
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

    SingletonRoomObject "1" *-down- "1" SingletonRoomDatabase
  }
}

namespace app.module #FFFFEE {
    class AppDatabase {
      + updateUserInfo(emailAddress: String, tokenInfo: TokenInfo)
      + updateSelectedAlbums(userInfoId: Long, albums: List<Album>)
      + updateDisplayedPhotos(selctedAlbumId: Long, mediaItems: List<MediaItem>)
      + findUserInfoByEmailAddress(emailAddress: String): UserInfo?
      + findUserInfoWithSelectedAlbums(emailAddress: String): UserInfoWithSelectedAlbums?
      + findSelectedAlbumWithDisplayedPhotos(id: Long): SelectedAlbumWithDisplayedPhotos?
    }
}

core.database.room.SingletonRoomDatabase o-right- core.database.dao.UserInfoDao
core.database.room.SingletonRoomDatabase o-right- core.database.dao.SelectedAlbumDao
core.database.room.SingletonRoomDatabase o-right- core.database.dao.DisplayedPhotoDao

app.module.AppDatabase -- core.database.room.SingletonRoomObject

@enduml
