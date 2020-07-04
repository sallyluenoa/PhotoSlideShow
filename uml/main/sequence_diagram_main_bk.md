@startuml
box "VIPER" #FFFFEE
  participant View
  participant Presenter
  participant Interactor
  participant Router
end box

box "Web API" #EEFFFF
  participant GoogleOAuth2Api
  participant PhotosLibraryApi
end box

box "Database" #FFEEFF
  participant UserInfoDatabase
  participant SelectedAlbumDatabase
  participant DisplayedMediaItemDatabase
end box

box "File" #E6FFE9
  participant PhotosDownloader
end box


View++
View -> Presenter++: requestUpdatePhotos
View--

Presenter -> Interactor++: isNeededUpdatePhotos
  opt this.userInfo==null
    Interactor -> UserInfoDatabase++: find
    return UserInfo
  end
return Callback#isNeededUpdatePhotosResult

opt !isNeededPhotosUpdateResult
  View <-- Presenter: Callback#requestUpdatePhotosResult
  Presenter -> Presenter++
  destroy Presenter
end

== Update AccessToken ==

Presenter -> Interactor++: isNeededUpdateAccessToken
  opt this.userInfo==null
    Interactor -> UserInfoDatabase++: find
    return UserInfo
  end
return Callback#isNeededUpdateAccessTokenResult

opt !isNeededUpdateAccessTokenResult
  Presenter -> Interactor++: requestUpdateAccessToken
  Interactor -> GoogleOAuth2Api++: requestTokenInfoWithRefreshToken
  return TokenInfo

  opt TokenInfo==null
    Presenter <-- Interactor: Callback#requestUpdateAccessTokenResult
    View <-- Presenter: Callback#requestUpdatePhotosResult
    Presenter -> Presenter++
    destroy Presenter
  end

  Interactor -> UserInfoDatabase: update
  Presenter <-- Interactor--: Callback#requestUpdateAccessTokenResult
end

== Decide Selected Album ==

Presenter -> Interactor++: hasSelectedAlbum
  opt this.selectedAlbum==null
    Interactor -> SelectedAlbumDatabase++: find
    return SelectedAlbum
  end
return Callback#hasSelectedAlbumResult

opt !hasSelectedAlbumResult
  Presenter -> Router: startSelectActivity
  [--> View
  View --> Presenter: evaluateActivityResult

  alt Has selected album?
    Presenter -> Interactor++: requestUpdateSelectedAlbum
    Interactor -> SelectedAlbumDatabase: update
    Interactor--
  else
    View <-- Presenter: Callback#requestUpdatePhotosResult
    Presenter -> Presenter++
    destroy Presenter
  end
end

== Download Images ==

Presenter -> Interactor++: requestDownloadMediaItems

  Interactor -> PhotosLibraryApi++: requestMediaItems
  return List<MediaItem>

  Interactor -> Interactor: Make random photo MediaItems

  Interactor -> PhotosDownloader++: requestDownloads
  return List<String>

  Interactor -> DisplayedMediaItemDatabase: replace

Presenter <-- Interactor--: Callback#requestDownloadMediaItemsResult

====

View <-- Presenter--: Callback#requestUpdatePhotosResult
@enduml
