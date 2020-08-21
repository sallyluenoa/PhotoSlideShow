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


[-> View
activate View
View -> View++: Show app logo 2 secs.

View -> Presenter++: requestSignIn
View--

== Runtime Permissions ==

Presenter -> Interactor++: isGrantedRuntimePermissions
return Boolean

opt !isGrantedRuntimePermissions
  Presenter -> Router: startRuntimePermissions
  [--> View
  View --> Presenter: evaluateRequestPermissionsResult

  Presenter -> Interactor++: isGrantedRuntimePermissions  
  return Boolean

  opt !isGrantedRuntimePermissions
    View <-- Presenter: Callback#requestSignInResult
    View -> View++: Show error dialog
    destroy View
  end
end

== Google Sign In ==

Presenter -> Interactor++: requestGoogleSilentSignIn
  Interactor -> GoogleWebApis++: requestSilentSignIn
  return ApiResult
return Callback#requestGoogleSilentSignInResult

alt ApiResult==SUCCEEDED

else ApiResult==INVALID

  Presenter -> Router: startGoogleSignInActivity
  [--> View
  View --> Presenter: evaluateActivityResult

  Presenter -> Interactor++: isSucceededGoogleUserSignIn
    Interactor -> GoogleWebApis++: isSucceededUserSignIn
    return Boolean  
  return Boolean

  opt !isSucceededGoogleUserSignIn
    View <-- Presenter: Callback#requestSignInResult
    View -> View++: Show error dialog
    destroy View
  end

else
    View <-- Presenter: Callback#requestSignInResult
    View -> View++: Show error dialog
    destroy View
end

== Update UserInfo ==

Presenter -> Interactor++: requestUpdateUserInfo

Interactor -> GoogleWebApis++: getSignedInEmailAddress
return String

Interactor -> AppDatabase++: findUserInfoByEmailAddress
return UserInfo?

Interactor -> GoogleWebApis++: requestUpdateTokenInfo
return TokenInfo?

alt TokenInfo!=null
  Interactor -> AppDatabase: updateUserInfo
else
  Interactor -> GoogleWebApis++: requestSignOut
  return ApiResult
  Presenter <-- Interactor: Callback#requestUpdateUserInfoResult
  View <-- Presenter: Callback#requestSignInResult
  View -> View++: Show error dialog
  destroy View
end

Presenter <-- Interactor--: Callback#requestUpdateUserInfoResult

====

Presenter -> Router: startMainActivity
View <-- Presenter--: Callback#requestSignInResult

View -> Presenter++: destroy
Presenter -> Interactor!!: destroy
destroy Presenter
destroy View
@enduml