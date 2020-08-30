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

  namespace app.select.SplashContract #EEEEEE {
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
      + isSucceededGoogleUserSignIn(data: Intent?): Boolean
    }
    interface InteractorCallback {
      + requestGoogleSilentSignInResult(isSucceeded: Boolean)
      + requestUpdateUserInfoResult(isSucceeded: Boolean)
    }
    interface Router {
      + startRuntimePermissions(activity: Activity, permissions: Array<String>, requestCode: Int)
      + startGoogleSignInActivity(activity: Activity, requestCode: Int)
      + startMainActivity(activity: Activity)
    }

    PresenterCallback -[hidden]-> Presenter
    Presenter -[hidden]-> InteractorCallback
    InteractorCallback -[hidden]-> Interactor
    Interactor -[hidden]-> Router
  }

  namespace entity {
    enum SignInRequest {
      RUNTIME_PERMISSIONS
      GOOGLE_SIGN_IN
      UPDATE_USER_INFO
      COMPLETED
      ==
      + code: Int
      + failedTitle: Int
      + failedMessage: Int
      __
      + next(): SignInRequest
    }
  }

  class SplashActivity {
    - fragmentManager: FragmentManager
    - replaceFragment(fragment: Fragment)
    - requestSignIn()
  }
  class SplashPresenter {
    - activity(): Activity?
    - presentSequence(request: SignInRequest)
  }
  class SplashInteractor {
    - appDatabase: AppDatabase
    - googleWebApis: GoogleWebApis
    - <b>[suspend]</b> updateUserInfo(): Boolean
  }
  class SplashRouter

  SplashActivity o-down-- SplashPresenter: - presenter
  SplashPresenter o-down-- SplashInteractor: - interactor
  SplashPresenter o-down--- SplashRouter: - router

  SplashPresenter .right.|> app.select.SplashContract.Presenter
  SplashInteractor .right.|> app.select.SplashContract.Interactor
  SplashRouter .right.|> app.select.SplashContract.Router

  SplashPresenter o-up- app.select.SplashContract.PresenterCallback: - callback
  SplashInteractor o-up- app.select.SplashContract.InteractorCallback: - callback

  SplashActivity .down.|> app.select.SplashContract.PresenterCallback
  SplashPresenter .down.|> app.select.SplashContract.InteractorCallback
}

app.select.SplashContract.PresenterCallback .right.|> core.viper.ViperContract.PresenterCallback
app.select.SplashContract.Presenter .right.|> core.viper.ViperContract.Presenter
app.select.SplashContract.InteractorCallback .right.|> core.viper.ViperContract.InteractorCallback
app.select.SplashContract.Interactor .right.|> core.viper.ViperContract.Interactor
app.select.SplashContract.Router .right.|> core.viper.ViperContract.Router
@enduml