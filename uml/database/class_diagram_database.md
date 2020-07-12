@startuml
namespace core.database #FFEEFF {

  namespace entity {
    interface BaseEntity {
      + id: Long
      + createDateTimeMillis: Long
      + updateDateTimeMillis: Long
    }
    class UserInfo << (D, sandybrown) >> {
      + emailAdress: String
      + accessToken: String
      + refreshToken: String
      + expiredAccessTokenTimeMillis: Long
      + updatePhotosTimeMillis: Long
    }
    class SelectedAlbum << (D, sandybrown) >> {
      + userInfoId: Long
      + albumId: String
      + albumTitle: String
      + coveredMediaItemId: String
    }
    class DisplayedPhoto << (D, sandybrown) >> {
      + selectedAlbumId: Long
      + mediaItemId: String
      + fileName: String
      + creationTime: Long
      + contributorName: String
      + isMyFavorite: Boolean
    }
    class UserInfoWithSelectedAlbums << (D, sandybrown) >>
    class SelectedAlbumWithDisplayedPhotos << (D, sandybrown) >>
    class SelectedAlbumWithCoveredPhoto << (D, sandybrown) >>

    UserInfo .down.|> BaseEntity
    SelectedAlbum .down.|> BaseEntity
    DisplayedPhoto .down.|> BaseEntity

    UserInfoWithSelectedAlbums "1" *-down- "1" UserInfo
    UserInfoWithSelectedAlbums "1" *-down- "*" SelectedAlbum
    SelectedAlbumWithDisplayedPhotos "1" *-down- "1" SelectedAlbum
    SelectedAlbumWithDisplayedPhotos "1" *-down- "*" DisplayedPhoto
    SelectedAlbumWithCoveredPhoto "1" *-down- "1" SelectedAlbum
    SelectedAlbumWithCoveredPhoto "1" *-down- "1" DisplayedPhoto
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
      + findWithSelectedAlbums(emailAddress: String): UserInfoWithSelectedAlbums?
    }
    interface SelectedAlbumDao {
      + findByUserInfoIdAndAlbumId(userInfoId: Long, albumId: String): SelectedAlbum?
      + findWithCoveredPhoto(albumId: String): SelectedAlbumWithCoveredPhoto?
      + findWithDisplayedPhotos(albumId: String): SelectedAlbumWithDisplayedPhotos?
    }
    interface DisplayedPhotoDao {
      + findBySelectedAlbumIdAndMediaItemId(selectedAlbumId: Long, mediaItemId: String): DisplayedPhoto?
    }

    UserInfoDao ..|> BaseDao
    SelectedAlbumDao ..|> BaseDao
    DisplayedPhotoDao ..|> BaseDao
  }

  namespace impl {
    class UserInfoDatabaseImpl {
      - dao(): UserInfoDao
    }
    class SelectedAlbumDatabaseImpl {
      - dao(): SelectedAlbumDao
    }
    class DisplayedPhotoDatabaseImpl {
      - dao(): DisplayedPhotoDao
    }
  }

  interface UserInfoDatabase {
    + update(emailAddress: String, tokenInfo: TokenInfo)
    + delete(email: String)
    + find(email: String): UserInfo?
    + findWithSelectedAlbums(email: String): UserInfoWithSelectedAlbums?
  }
  interface SelectedAlbumDatabase {
    + update(userInfoId: Long, album: Album)
    + delete(userInfoId: Long, albumId: String)
    + findWithCoveredPhoto(albumId: String): SelectedAlbumWithCoveredPhoto?
    + findWithDisplayedPhotos(albumId: String): SelectedAlbumWithDisplayedPhotos?
  }
  interface DisplayedPhotoDatabase {
    + update(selectedAlbumId: Long, mediaItem: MediaItem)
  }
}

core.database.impl.UserInfoDatabaseImpl .up.|> core.database.UserInfoDatabase
core.database.impl.SelectedAlbumDatabaseImpl .up.|> core.database.SelectedAlbumDatabase
core.database.impl.DisplayedPhotoDatabaseImpl .up.|> core.database.DisplayedPhotoDatabase

core.database.impl.UserInfoDatabaseImpl o-down- core.database.room.SingletonRoomObject
core.database.impl.SelectedAlbumDatabaseImpl o-down- core.database.room.SingletonRoomObject
core.database.impl.DisplayedPhotoDatabaseImpl o-down- core.database.room.SingletonRoomObject

core.database.room.SingletonRoomDatabase o-down- core.database.dao.UserInfoDao
core.database.room.SingletonRoomDatabase o-down- core.database.dao.SelectedAlbumDao
core.database.room.SingletonRoomDatabase o-down- core.database.dao.DisplayedPhotoDao

@enduml
