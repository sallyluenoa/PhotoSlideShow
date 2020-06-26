@startuml
box "VIPER" #FFFFEE
  participant View
  participant Presenter
  participant Interactor
  participant Router
end box

box "Web API" #EEFFFF
  participant GoogleSignInApi
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
View -> Presenter++: requestUpdatePhotoData
View--

Presenter -> Interactor++: isNeededPhotosUpdate
  Interactor -> UserInfoDatabase++: find
  return UserInfo
return Boolean

opt !isNeededPhotosUpdate
  Presenter -> Presenter++
  destroy Presenter
end

== Update AccessToken ==

Presenter -> Interactor++: isNeededUpdateAccessToken
return Boolean

opt !isNeededUpdateAccessToken
  Presenter -> Interactor++: requestUpdateAccessToken
  Interactor -> GoogleOAuth2Api++: requestTokenInfoWithRefreshToken
  return TokenInfo

  opt TokenInfo==null
    Presenter <-- Interactor: Callback#requestUpdateAccessTokenResult
    Presenter -> Presenter++
    destroy Presenter
  end

  Interactor -> UserInfoDatabase: update
  Presenter <-- Interactor--: Callback#requestUpdateAccessTokenResult
end

== Decide Selected Album ==

Presenter -> Interactor++: hasSelectedAlbum
  Interactor -> SelectedAlbumDatabase++: find
  return SelectedAlbum
return Boolean

opt !hasSelectedAlbum
  Presenter -> Router: startSelectActivity
  [--> View
  View --> Presenter: evaluateActivityResult

  alt Has selected albumId?
    Presenter -> Interactor++: updateSelectedAlbumInfo
    Interactor -> SelectedAlbumDatabase: update
    Interactor--
  else
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

Presenter <-- Interactor--: requestDownloadMediaItemsResult

====

View <-- Presenter--: notifyUpdatedPhotoData
@enduml
