@startuml
box "VIPER" #FFFFEE
  participant View
  participant Presenter
  participant Interactor
  participant Router
end box

box "Database" #FFEEFF
  participant AppDatabase
end box

box "File" #E6FFE9
  participant PhotosDownloader
end box

box "Web API" #EEFFFF
  participant PhotosLibraryApi
end box

View++
View -> Presenter++: requestLoadDisplayedPhotos
  Presenter -> Interactor++: requestLoadDisplayedPhotos

  opt this.userInfoData.id == 0
    Interactor -> AppDatabase++: findUserInfoDataByEmailAddress
    return UserInfoData?
  end

  return Callback#requestLoadDisplayedPhotos
return Callback#requestLoadDisplayedPhotos

View -> Presenter++: requestUpdateDisplayedPhotos
View--

== Config Update ==

Presenter -> Interactor++: isNeededUpdatePhotos
return Boolean

opt !isNeededUpdatePhotos
  View <-- Presenter: Callback#requestUpdatePhotosResult
  Presenter -> Presenter++
  destroy Presenter
end

== Decide Selected Album ==

Presenter -> Interactor++: hasSelectedAlbums
return Boolean

opt !hasSelectedAlbums
  Presenter -> Router: startSelectActivity
  [--> View
  View --> Presenter: evaluateActivityResult

  opt Is selected album not found or canceled?
    View <-- Presenter: Callback#requestUpdatePhotosResult
    Presenter -> Presenter++
    destroy Presenter
  end
end

== Download Images ==

Presenter -> Interactor++: requestDownloadPhotos

  loop albums.foreach
    Interactor -> PhotosLibraryApi++: requestMediaItems
    return List<MediaItem>

    Interactor -> Interactor: Make random photo MediaItems

    Interactor -> PhotosDownloader++: requestDownloads
    return List<String>
  end

return Callback#requestDownloadPhotosResult

== Update Database ==

Presenter -> Interactor++: requestUpdateDatabase
  Interactor -> AppDatabase: replaceSelectedData
  Interactor -> AppDatabase++: findUserInfoDataById
  return UserInfoData?
return Callback#requestUpdateDatabaseResult

====

View <-- Presenter--: Callback#requestUpdatePhotosResult
@enduml
