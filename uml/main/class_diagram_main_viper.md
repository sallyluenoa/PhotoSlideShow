@startuml
namespace app.splash #FFFFEE {
  SplashActivity o-- SplashPresenter: -presenter
  SplashPresenter o--- SplashInteractor: -interactor
  SplashPresenter o---- SplashRouter: -router

  SplashPresenter .|> app.splash.contract.SplashContract.Presenter
  SplashInteractor .|> app.splash.contract.SplashContract.Interactor
  SplashRouter .|> app.splash.contract.SplashContract.Router

  SplashActivity ..|> app.splash.contract.SplashContract.PresenterCallback
  SplashPresenter o-- app.splash.contract.SplashContract.PresenterCallback: -callback
  SplashPresenter ..|> app.splash.contract.SplashContract.InteractorCallback
  SplashInteractor o-- app.splash.contract.SplashContract.InteractorCallback: -callback

  app.splash.contract.SplashContract.Presenter -[hidden]> SplashPresenter
  app.splash.contract.SplashContract.Interactor -[hidden]> SplashInteractor
  app.splash.contract.SplashContract.Router -[hidden]> SplashRouter

  namespace entity {
    enum UpdatePhotoRequest {
      SKIPPED
      SELECT_ALBUM
      DOWNLOAD_PHOTOS
      COMPLETED
      UNKOWN
    }

    UpdatePhotoRequest -[hidden]-> app.splash.SplashActivity
  }
}

namespace app.splash.contract.SplashContract #EEEEEE {
  interface Presenter {
    + requestLoadDisplayedPhotos()
    + requestUpdateDisplayedPhotos()
    + evaluateActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
  }
  interface PresenterCallback {
    + requestLoadDisplayedPhotosResult()
    + requestUpdateDisplayedPhotosResult()
  }
  interface Interactor {
    + requestLoadDisplayedPhotos()
    + requestUpdateSelectedAlbums()
    + requestDownloadPhotos()
    + isNeededUpdatePhotos(): Boolean
    + hasSelectedAlbum(): Boolean
  }
  interface InteractorCallback {
    + requestLoadDisplayedPhotosResult(isSucceeded: Boolean)
    + requestUpdateSelectedAlbumsResult(isSucceeded: Boolean)
    + requestDownloadPhotosResult(isSucceeded: Boolean)
  }
  interface Router {
    + startSelectActivity(activity: Activity)
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

app.splash +-- app.splash.contract.SplashContract

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
