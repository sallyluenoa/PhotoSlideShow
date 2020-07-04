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
    enum SignInRequest {
      RUNTIME_PERMISSIONS
      GOOGLE_SIGN_IN
      UPDATE_USER_INFO
      COMPLETED
    }

    SignInRequest -[hidden]-> app.splash.SplashActivity
  }
}

namespace app.splash.contract.SplashContract #EEEEEE {
  interface Presenter {
    + requestSignIn()
    + evaluateActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    + evaluateRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
  }
  interface PresenterCallback {
    + requestSignInResult(request: SignInRequest)
  }
  interface Interactor {
    + requestGoogleSilentSignIn()
    + requestUpdateUserInfo()
    + isGrantedRuntimePermissions(permissions: Array<String>): Boolean
    + isGoogleSignedIn()
    + isSucceededGoogleUserSignIn(data: Intent?): Boolean
  }
  interface InteractorCallback {
    + requestGoogleSilentSignInResult(isSucceeded: Boolean)
    + requestUpdateUserInfoResult(isSucceeded: Boolean)
  }
  interface Router {
    + startRuntimePermissions(activity: Activity, permissions: Array<String>, requestCode: Int)
    + startGoogleSignInActivity(activity: Activity, clientHolder: GoogleSignInClientHolder, requestCode: Int)
    + startMainActivity(activity: Activity)
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
    + destroy()
  }
  interface PresenterCallback {
    + getActivity(): Activity
  }
  interface Interactor {
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