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

  namespace app.select.SelectContract #EEEEEE {
    interface Presenter {
      + requestLoadSharedAlbums()
    }
    interface PresenterCallback {
      + requestLoadSharedAlbumsResult(albums: List<Album>)
    }
    interface Interactor {
      + requestLoadSharedAlbums()
    }
    interface InteractorCallback {
      + requestLoadSharedAlbumsResult(albums: List<Album>)
    }

    PresenterCallback -[hidden]-> Presenter
    Presenter -[hidden]-> InteractorCallback
    InteractorCallback -[hidden]-> Interactor
  }

  class SelectActivity {
    - fragmentManager: FragmentManager
    + decidedAndFinishAlbum(album: Album)
    - replaceFragment(fragment: Fragment)
  }
  class SelectPresenter
  class SelectInteractor {
    - appDatabase: AppDatabase
    - googleWebApis: GoogleWebApis
    - <b>[suspend]</b> loadSharedAlbumsResult(): List<Album>
  }

  SelectActivity o-down-- SelectPresenter: - presenter
  SelectPresenter o-down-- SelectInteractor: - interactor

  SelectPresenter .right.|> app.select.SelectContract.Presenter
  SelectInteractor .right.|> app.select.SelectContract.Interactor

  SelectPresenter o-up- app.select.SelectContract.PresenterCallback: - callback
  SelectInteractor o-up- app.select.SelectContract.InteractorCallback: - callback

  SelectActivity .down.|> app.select.SelectContract.PresenterCallback
  SelectPresenter .down.|> app.select.SelectContract.InteractorCallback
}

app.select.SelectContract.PresenterCallback .right.|> core.viper.ViperContract.PresenterCallback
app.select.SelectContract.Presenter .right.|> core.viper.ViperContract.Presenter
app.select.SelectContract.InteractorCallback .right.|> core.viper.ViperContract.InteractorCallback
app.select.SelectContract.Interactor .right.|> core.viper.ViperContract.Interactor
@enduml