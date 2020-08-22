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

box "Web API" #EEFFFF
  participant GoogleWebApis
end box

== Load Shared Albums ==

View++
View -> Presenter++: requestLoadSharedAlbums
  Presenter -> Interactor++: requestLoadSharedAlbums

    Interactor -> GoogleWebApis++: requestSharedAlbums
    return PhotosApiResult<Album>

    opt Is result.tokenInfo updated?
      Interactor -> AppDatabase: updateUserInfo
    end

  return Callback#requestLoadSharedAlbumsResult
return Callback#requestLoadSharedAlbumsResult

View -> View: Show shared albums.
[<-- View--

== Selected by User ==

[-> View++: User select.

opt Album is selected by user
  View -> View: set RESULT_OK and Album
end

View -> Presenter++: destroy
Presenter -> Interactor!!: destroy
destroy Presenter
destroy View
@enduml