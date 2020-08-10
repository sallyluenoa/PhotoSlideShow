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

  opt this.userInfo.id == 0
    Interactor -> AppDatabase++: findUserInfoWithSelectedAlbums
    return UserInfoWithSelectedAlbums?
    Interactor -> Interactor: Update this.userInfo
    Interactor -> Interactor: Update this.selectedAlbums

    loop selectedAlbums.foreach
      Interactor -> AppDatabase++: findSelectedAlbumWithDisplayedPhotos
      return SelectedAlbumWithDisplayedPhotos?
      Interactor -> Interactor: Update this.displayedPhotos
    end
  end

  return List<DisplayedPhoto>
return List<DisplayedPhoto>

View -> Presenter++: requestUpdateDisplayedPhotos
View--

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

  alt Has selected album?
    Presenter -> Interactor++: requestUpdateSelectedAlbum
    Interactor -> AppDatabase: updateSelectedAlbums
    Interactor -> AppDatabase++: findUserInfoWithSelectedAlbums
    return UserInfoWithSelectedAlbums?
    Interactor -> Interactor: Update this.selectedAlbums
  else
    View <-- Presenter: Callback#requestUpdatePhotosResult
    Presenter -> Presenter++
    destroy Presenter
  end
end

== Download Images ==

Presenter -> Interactor++: requestDownloadPhotos

  loop this.selectedAlbums.foreach
    Interactor -> PhotosLibraryApi++: requestMediaItems
    return List<MediaItem>
    Interactor -> Interactor: Make random photo MediaItems
  end

  Interactor -> PhotosDownloader++: requestDownloads
  return List<String>

  Interactor -> AppDatabase: replace

Presenter <-- Interactor--: Callback#requestDownloadMediaItemsResult

====

View <-- Presenter--: Callback#requestUpdatePhotosResult
@enduml
