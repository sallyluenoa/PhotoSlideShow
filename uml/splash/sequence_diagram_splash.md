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
  participant GoogleSignInApi
  participant GoogleOAuth2Api
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

Presenter -> Interactor++: isGoogleSignedIn
  Interactor -> GoogleSignInApi++: isSignedInAccount
  return Boolean
return Boolean

alt isGoogleSignedIn
  Presenter -> Interactor++: requestGoogleSilentSignIn
    Interactor -> GoogleSignInApi++: requestSilentSignIn
    return ApiResult
  return Callback#requestGoogleSilentSignInResult

  opt ApiResult!=Succeeded
    View <-- Presenter: Callback#requestSignInResult
    View -> View++: Show error dialog
    destroy View
  end

else
  Presenter -> Router: startGoogleSignInActivity
  [--> View
  View --> Presenter: evaluateActivityResult

  Presenter -> Interactor++: isSucceededGoogleUserSignIn
    Interactor -> GoogleSignInApi++: isSucceededUserSignIn
    return Boolean  
  return Boolean

  opt !isSucceededGoogleUserSignIn
    View <-- Presenter: Callback#requestSignInResult
    View -> View++: Show error dialog
    destroy View
  end
end

== Update UserInfo ==

Presenter -> Interactor++: requestUpdateUserInfo

Interactor -> GoogleSignInApi++: getSignedInAccount
return GoogleSignInAccount

Interactor -> AppDatabase++: findUserInfoByEmailAddress
return UserInfo?

opt UserInfo!=null
  Interactor -> GoogleOAuth2Api++: requestTokenInfoWithRefreshToken
  return TokenInfo?
end

opt TokenInfo==null
  Interactor -> GoogleOAuth2Api++: requestTokenInfoWithAuthCode
  return TokenInfo?
end

alt TokenInfo!=null
  Interactor -> AppDatabase: updateUserInfo
else
  Interactor -> GoogleSignInApi++: revokeAccess
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
