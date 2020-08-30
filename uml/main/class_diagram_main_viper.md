@startuml
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

namespace app.select #FFFFEE {

  namespace app.select.MainContract #EEEEEE {
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
      + requestDownloadPhotos(context: Context, albums: List<Album>?)
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
  }

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
  }

  class MainActivity {
    - fragmentManager: FragmentManager
    - displayedPhotos: List<DisplayedPhoto>
    - displayedIndex: Integer
    - isRequestingUpdatePhotos: Boolean
    - replaceFragment(fragment: Fragment)
    - presentImage(filePath: String)
    - presentSlideShow()
    - requestLoadDisplayedPhotos()
    - requestUpdateDisplayedPhotos()
    - <b>[synchronized]</b> getDisplayedPhoto(): DisplayedPhoto?
    - <b>[synchronized]</b> updateDisplayedPhotos(displayedPhotos: List<DisplayedPhoto>)
  }
  class MainPresenter {
    - activity(): Activity?
    - presentSequence(request: UpdatePhotosRequest, value: Any?)
  }
  class MainInteractor {
    - appDatabase: AppDatabase
    - photosDownloader: PhotosDownloader
    - googleWebApis: GoogleWebApis
    - userInfoData: UserInfoData
    - <b>[suspend]</b> loadDisplayedPhotos(): List<DisplayedPhoto>
    - <b>[suspend]</b> downloadPhotos(context: Context, albums: List<Album>?): List<AppDatabase.PhotoInfo>
    - <b>[suspend]</b> updateDatabase(photosInfo: List<AppDatabase.PhotoInfo>): Boolean
    - getOutputDir(context: Context): File?
    - convertMediaItems(mediaItems: List<MediaItem>, size: Int): List<MediaItem>
  }
  class MainRouter

  MainActivity o-down-- MainPresenter: - presenter
  MainPresenter o-down-- MainInteractor: - interactor
  MainPresenter o-down--- MainRouter: - router

  MainPresenter .right.|> app.select.MainContract.Presenter
  MainInteractor .right.|> app.select.MainContract.Interactor
  MainRouter .right.|> app.select.MainContract.Router

  MainPresenter o-up- app.select.MainContract.PresenterCallback: - callback
  MainInteractor o-up- app.select.MainContract.InteractorCallback: - callback

  MainActivity .down.|> app.select.MainContract.PresenterCallback
  MainPresenter .down.|> app.select.MainContract.InteractorCallback
}

app.select.MainContract.PresenterCallback .right.|> core.viper.ViperContract.PresenterCallback
app.select.MainContract.Presenter .right.|> core.viper.ViperContract.Presenter
app.select.MainContract.InteractorCallback .right.|> core.viper.ViperContract.InteractorCallback
app.select.MainContract.Interactor .right.|> core.viper.ViperContract.Interactor
app.select.MainContract.Router .right.|> core.viper.ViperContract.Router
@enduml