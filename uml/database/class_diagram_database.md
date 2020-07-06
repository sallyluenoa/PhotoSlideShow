@startuml
namespace core.database #FFEEFF {

  namespace entity {
    interface BaseEntity {
      + id: Long
      + createDateTimeMillis: Long
      + updateDateTimeMillis: Long
    }
    class UserInfo <<(D, sandybrown)>> {
      + emailAdress: String
      + accessToken: String
      + refreshToken: String
      + expiredAccessTokenTimeMillis: Long
      + updatePhotosTimeMillis: Long
    }
    class SelectedAlbum <<(D, sandybrown)>> {
      + userId: Long
      + albumId: String
      + albumTitle: String
      + coverMediaItemId: String
    }
    class DisplayedPhotos <<(D, sandybrown)>> {
      + selectedAlbumId: Long
      + mediaItemId: String
      + fileName: String
      + isCoveredAlbum: Boolean
    }

    UserInfo .up.|> BaseEntity
    SelectedAlbum  .up.|> BaseEntity
    DisplayedPhotos .up.|> BaseEntity
  }

  namespace room {
    abstract class AppRoomDatabase {
      + {abstract} userInfoDao(): UserInfoDao
      + {abstract} selectedAlbumDao(): SelectedAlbumDao
      + {abstract} displayedPhotosDao(): DisplayedPhotosDao
    }
  }

  namespace dao {
    interface BaseDao<KeyT, EntityT> {
      + insert(entity: EntityT)
      + update(entity: EntityT)
      + delete(entity: EntityT)
      + getAll(): List<EntityT>
      + findById(id: long): List<EntityT>
      + findByKey(key: KeyT): List<EntityT>
    }
    interface UserInfoDao
    interface SelectedAlbumDao
    interface DisplayedPhotosDao

    UserInfoDao ..|> BaseDao
    SelectedAlbumDao ..|> BaseDao
    DisplayedPhotosDao ..|> BaseDao
  }

  namespace impl {
    class UserInfoDatabaseImpl
    class SelectedAlbumDatabaseImpl
    class DisplayedPhotosDatabaseImpl
  }

  interface BaseDatabase<KeyT, EntityT, ResourceT> {
    + update(resource: ResourceT): Boolean
    + deleteById(id: long)
    + deleteByKey(key: KeyT)
    + getAll(): List<EntityT>
    + findById(id: long): EntityT?
    + findByKey(key: KeyT): EntityT?
    # dao(): BaseDao
  }
  interface UserInfoDatabase
  interface SelectedAlbumDatabase
  interface DisplayedPhotosDatabase

  UserInfoDatabase .up.|> BaseDatabase
  SelectedAlbumDatabase .up.|> BaseDatabase
  DisplayedPhotosDatabase .up.|> BaseDatabase
}

core.database.impl.UserInfoDatabaseImpl .up.|> core.database.UserInfoDatabase
core.database.impl.SelectedAlbumDatabaseImpl .up.|> core.database.SelectedAlbumDatabase
core.database.impl.DisplayedPhotosDatabaseImpl .up.|> core.database.DisplayedPhotosDatabase

core.database.impl.UserInfoDatabaseImpl o-down- core.database.room.AppRoomDatabase
core.database.impl.SelectedAlbumDatabaseImpl o-down- core.database.room.AppRoomDatabase
core.database.impl.DisplayedPhotosDatabaseImpl o-down- core.database.room.AppRoomDatabase

core.database.room.AppRoomDatabase o-down- core.database.dao.UserInfoDao
core.database.room.AppRoomDatabase o-down- core.database.dao.SelectedAlbumDao
core.database.room.AppRoomDatabase o-down- core.database.dao.DisplayedPhotosDao

@enduml
