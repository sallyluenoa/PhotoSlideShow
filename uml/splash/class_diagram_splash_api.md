@startuml
namespace app.splash #FFFFEE {
  class SplashInteractor
}

namespace core.webapi #EEFFFF {
  interface GoogleSignInApi {
    + requestSilentSignIn(): ApiResult
    + requestSignOut(): ApiResult
    + requestRevokeAccess(): ApiResult
    + {static} isSignedInAccount(context: Context): Boolean
    + {static} getSignedInAccount(context: Context): GoogleSignInAccount?
    + {static} isSucceededUserSignIn(data: Intent?): Boolean
  }
  interface GoogleOAuth2Api {
    + requestTokenInfoWithAuthCode(serverAuthCode: String): TokenInfo?
    + requestTokenInfoWithRefreshToken(refreshToken: String): TokenInfo?
  }

  GoogleSignInApi -[hidden]-> GoogleOAuth2Api

  namespace client {
    class GoogleSignInClientHolder {
      + client: GoogleSignInClient
    }
  }

  namespace entity {
    enum ApiResult {
      SUCCEEDED
      FAILED
      CANCELED
    }
    enum PhotoScope {
      READ_WRITE
      READ_ONLY
      WRITE_ONLY
      READ_ONLY_APP
      SHARING
    }
    class TokenInfo {
      + accessToken: String
      + refreshToken: String
      + expiredAccessTokenTimeMillis: Long
    }
  }
}

namespace core.database #FFEEFF {
  interface UserInfoDatabase {
    + update(email: String, tokenInfo: TokenInfo): Boolean
    + delete(email: String)
    + find(email: String): UserInfo?
  }
  interface BaseDatabase<T> {
    + getAll(): List<T>
  }

  UserInfoDatabase ..|> BaseDatabase

  namespace entity {
    class UserInfo {
      + id: Long
      + emailAddress: String
      + accessToken: String
      + refreshToken: String
      + expiredAccessTokenTimeMillis: Long
      + updateDateTimeMillis: Long
    }
  }
}

app.splash.SplashInteractor o-- core.webapi.GoogleSignInApi: -signInApi
app.splash.SplashInteractor o-- core.webapi.GoogleOAuth2Api: -oauth2Api
app.splash.SplashInteractor o-- core.database.UserInfoDatabase: -database

@enduml
