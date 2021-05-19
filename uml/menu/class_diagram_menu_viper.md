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

  namespace app.select.MenuContract #EEEEEE {
    interface Presenter {
      + requestShowLicenses()
      + requestChangeUser()
      + requestSignOut()
    }
    interface PresenterCallback {
      + onCreateResult(accountName: String, emailAddress: String)
      + onFailedChangeUser()
      + onFailedSignOut()
    }
    interface Interactor {
      + requestChangeUser()
      + requestSignOut()
    }
    interface InteractorCallback {
      + onCreateResult(accountName: String, emailAddress: String)
      + onChangeUserResult(result: ApiResult)
      + onSignOutResult(result: ApiResult)
    }
    interface Router {
      + startSplashActivityAndFinishAll(activity: Activity)
      + startOssLicensesMenuActivity(activity: Activity, titleResId: Int)
    }

    PresenterCallback -[hidden]-> Presenter
    Presenter -[hidden]-> InteractorCallback
    InteractorCallback -[hidden]-> Interactor
    Interactor -[hidden]-> Router
  }

  enum DialogRequest {
    CONFIRM_CHANGE_USER
    CONFIRM_SIGN_OUT
    FAILED_CHANGE_USER
    FAILED_SIGN_OUT
    ==
    + code: Int
    - titleResId: Int?
    - messageResId: Int?
    - positiveResId: Int?
    - negativeResId: Int?
    - cancelable: Boolean
    __
    + {static} convertFromCode(code: Int): DialogRequest
    + show(context: Context, fragmentManager: FragmentManager)
  }
  class MenuActivity {
    - binding: ActivityMenuBinding
    - replaceFragment(fragment: Fragment, addStack: Boolean)
    - showDialogFragment(request: DialogRequest)
  }
  class MenuPresenter {
    - activity(): Activity?
  }
  class MenuInteractor {
    - appDatabase: AppDatabase
    - googleWebApis: GoogleWebApis
    - createLoad()
    - <b>[suspend]</b> changeUser(): ApiResult
    - <b>[suspend]</b> signOut(): ApiResult
  }
  class MenuRouter

  MenuActivity +-right- DialogRequest: private
  MenuActivity o-down-- MenuPresenter: - presenter
  MenuPresenter o-down-- MenuInteractor: - interactor
  MenuPresenter o-down--- MenuRouter: - router

  MenuPresenter .right.|> app.select.MenuContract.Presenter
  MenuInteractor .right.|> app.select.MenuContract.Interactor
  MenuRouter .right.|> app.select.MenuContract.Router

  MenuPresenter o-up- app.select.MenuContract.PresenterCallback: - callback
  MenuInteractor o-up- app.select.MenuContract.InteractorCallback: - callback

  MenuActivity .down.|> app.select.MenuContract.PresenterCallback
  MenuPresenter .down.|> app.select.MenuContract.InteractorCallback
}

app.select.MenuContract.PresenterCallback .right.|> core.viper.ViperContract.PresenterCallback
app.select.MenuContract.Presenter .right.|> core.viper.ViperContract.Presenter
app.select.MenuContract.InteractorCallback .right.|> core.viper.ViperContract.InteractorCallback
app.select.MenuContract.Interactor .right.|> core.viper.ViperContract.Interactor
app.select.MenuContract.Router .right.|> core.viper.ViperContract.Router
@enduml