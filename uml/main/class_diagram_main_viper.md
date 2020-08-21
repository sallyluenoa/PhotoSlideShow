@startuml
namespace app.splash #FFFFEE {
  namespace entity {
    enum UpdatePhotoRequest {
      SKIPPED
      SELECT_ALBUM
      DOWNLOAD_PHOTOS
      UPDATE_DATABASE
      COMPLETED
      UNKOWN
      ==
      + code: Int
      __
    }

    UpdatePhotoRequest -[hidden]-> app.splash.MainActivity
  }

  class MainActivity {
    - fragmentManager: FragmentManager
    - displayedPhotos: List<DisplayedPhoto>
    - displayedIndex: Integer
    - isForeground: Boolean
    - isUpdateRequested: Boolean
    - isRunningSlideShow: Boolean
    - replaceFragment(fragment: Fragment)
    - presentImage(filePath: String)
    - presentSlideShow()
  }
  class MainPresenter
  class MainInteractor {
    - context: Context
    - appDatabase: AppDatabase
    - photosDownloader: PhotosDownloader
    - googleWebApis: GoogleWebApis
    - <b>[suspend]</b> requestLoadDisplayedPhotosResult(displayedPhotos: List<DisplayedPhoto>)
    - <b>[suspend]</b> requestDownloadPhotosResult(photosInfo: List<AppDatabase.PhotoInfo>)
    - <b>[suspend]</b> requestUpdateDatabaseResult(isSucceeded: Boolean)
    - <b>[suspend]</b> requestDownloadPhotosInner(albums: List<Album>)
    - convertMediaItems(mediaItems: List<MediaItem>, size: Int): List<MediaItem>
  }
  class MainRouter

  MainActivity o-- MainPresenter: - presenter
  MainPresenter o--- MainInteractor: - interactor
  MainPresenter o---- MainRouter: - router

  MainPresenter .|> app.splash.contract.MainContract.Presenter
  MainInteractor .|> app.splash.contract.MainContract.Interactor
  MainRouter .|> app.splash.contract.MainContract.Router

  MainActivity ..|> app.splash.contract.MainContract.PresenterCallback
  MainPresenter o-- app.splash.contract.MainContract.PresenterCallback: - callback
  MainPresenter ..|> app.splash.contract.MainContract.InteractorCallback
  MainInteractor o-- app.splash.contract.MainContract.InteractorCallback: - callback

  app.splash.contract.MainContract.Presenter -[hidden]> MainPresenter
  app.splash.contract.MainContract.Interactor -[hidden]> MainInteractor
  app.splash.contract.MainContract.Router -[hidden]> MainRouter
}

namespace app.splash.contract.MainContract #EEEEEE {
  interface Presenter {
    + requestLoadDisplayedPhotos()
    + requestUpdateDisplayedPhotos()
    + evaluateActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
  }
  interface PresenterCallback {
    + requestLoadDisplayedPhotosResult(displayedPhotos: List<DisplayedPhoto>)
    + requestUpdateDisplayedPhotosResult(request: UpdatePhotosRequest)
  }
  interface Interactor {
    + requestLoadDisplayedPhotos()
    + requestDownloadPhotos()
    + requestDownloadPhotos(albums: List<Album>)
    + requestUpdateDatabase(photosInfo: List<PhotoInfo>)
    + isNeededUpdatePhotos(): Boolean
    + hasSelectedAlbum(): Boolean
  }
  interface InteractorCallback {
    + requestLoadDisplayedPhotosResult(displayedPhotos: List<DisplayedPhoto>)
    + requestDownloadPhotosResult(photosInfo: List<PhotoInfo>)
    + requestUpdateDatabaseResult(isSucceeded: Boolean)
  }
  interface Router {
    + startSelectActivity(activity: Activity, requestCode: Int)
  }

  PresenterCallback -[hidden]-> Presenter
  Presenter -[hidden]-> InteractorCallback
  InteractorCallback -[hidden]-> Interactor
  Interactor -[hidden]-> Router

  Presenter .|> core.viper.ViperContract.Presenter
  PresenterCallback .|> core.viper.ViperContract.PresenterCallback
  Interactor .|> core.viper.ViperContract.Interactor
  InteractorCallback .|> core.viper.ViperContract.InteractorCallback
  Router .|> core.viper.ViperContract.Router
}

app.splash +-- app.splash.contract.MainContract

namespace core.viper.ViperContract #DDDDDD {
  interface Presenter {
    + create(callback: PresenterCallback)
    + destroy()
  }
  interface PresenterCallback {
    + getActivity(): Activity
  }
  interface Interactor {
    + create(callback: InteractorCallback)
    + destroy()
  }
  interface InteractorCallback
  interface Router

  PresenterCallback -[hidden]-> Presenter
  Presenter -[hidden]-> InteractorCallback
  InteractorCallback -[hidden]-> Interactor
  Interactor -[hidden]-> Router
}
@enduml