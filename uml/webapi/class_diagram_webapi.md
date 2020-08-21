@startuml
namespace core.webapi #EEFFFF {

  namespace entity {
    enum ApiResult {
      SUCCEEDED
      FAILED
      CANCELED
      INVALID
      ==
      __
    }
    enum PhotoScope {
      READ_WRITE
      READ_ONLY
      WRITE_ONLY
      READ_ONLY_APP
      SHARING
      ==
      - value: String
      __
      + scope(): Scope
    }
    class WebInfo << (D, sandybrown) >> {
      + clientId: String,
      + projectId: String,
      + authUri: String,
      + tokenUri: String,
      + authProviderX509CertUrl: String,
      + clientSecret: String,
      + redirectUris: List<String>,
      + javascriptOrigins: List<String>
    }
    class ClientSecret << (D, sandybrown) >>
    class TokenInfo << (D, sandybrown) >> {
      + accessToken: String
      + refreshToken: String
      - expiredAccessTokenTimeMillis: Long
      + isAvailableAccessToken(): Boolean
      + afterUpdated(tokenInfo: TokenInfo): Boolean
    }

    ClientSecret *-- WebInfo: + web
  }

  namespace holder {
    class SingletonWebHolder << (O, gold) Singleton >> {
      + okHttpClient: OkHttpClient
      + googleSignInClient: GoogleSignInClient
      + photosLibraryClient: PhotosLibraryClient?
      + loadClientSecret(assetsFileReader: AssetsFileReader, jsonFileName: String)
      + setupOkHttpClient(connectionTimeoutMilliSecs: Long, readTimeoutMilliSecs: Long, writeTimeoutMilliSecs: Long)
      + setupGoogleSignInClient(context: Context, scopes: List<PhotoScope>, requestIdToken: Boolean, requestServerAuthCode: Boolean)
      + updatePhotosLibraryClient(tokenInfo: TokenInfo?)
    }
  }

  namespace impl {
    class GoogleSignInApiImpl {
      - context: Context
    }
    class GoogleOAuth2ApiImpl
    class PhotosLibraryApiImpl {
      - photosLibraryClient(): PhotosLibraryClient
    }
  }

  interface GoogleSignInApi {
    + <B>[suspend]</B> requestSilentSignIn(): ApiResult
    + <B>[suspend]</B> requestSignOut(): ApiResult
    + <B>[suspend]</B> requestRevokeAccess(): ApiResult
    + getSignedInAccount(context: Context): GoogleSignInAccount?
    + isSucceededUserSignIn(data: Intent?): Boolean
  }
  interface GoogleOAuth2Api {
    + <B>[suspend]</B> requestTokenInfoWithAuthCode(serverAuthCode: String): TokenInfo?
    + <B>[suspend]</B> requestTokenInfoWithRefreshToken(refreshToken: String): TokenInfo?
  }
  interface PhotosLibraryApi {
    + <B>[suspend]</B> requestAlbum(albumId: String): Album
    + <B>[suspend]</B> requestMediaItem(mediaItemId: String): MediaItem
    + <B>[suspend]</B> requestUpdateAlbums(albums: List<Album>): List<Album>
    + <B>[suspend]</B> requestUpdateMediaItems(mediaItems: List<MediaItem>): List<MediaItem>
    + <B>[suspend]</B> requestSharedAlbums(): List<Album>
    + <B>[suspend]</B> requestMediaItems(album: Album): List<MediaItem>
    + updatePhotosLibraryClient(tokenInfo: TokenInfo?)
    + currentTokenInfo(): TokenInfo
  }
}

namespace app.module #FFFFEE {
  class PhotosApiResult<ResultT : Serializable> << (D, sandybrown) >> {
    + photosResults: List<ResultT>
  }
  class GoogleWebApis {
    + <B>[suspend]</B> requestSilentSignIn(): ApiResult
    + <B>[suspend]</B> requestSignOut(withRevokeAccess: Boolean): ApiResult
    + <B>[suspend]</B> requestUpdateTokenInfo(oldTokenInfo: TokenInfo?): TokenInfo?
    + <B>[suspend]</B> requestSharedAlbums(): PhotosApiResult<Album>
    + <B>[suspend]</B> requestMediaItems(album: Album): PhotosApiResult<MediaItem>
    + getSignedInAccount(): GoogleSignInAccount
    + getSignedInEmailAddress(): String
    + isSignedInAccount(): Boolean
    + isSucceededUserSignIn(data: Intent?): Boolean
    - <B>[suspend]</B> updateTokenInfo(): TokenInfo?
  }
}

app.module.PhotosApiResult *-- core.webapi.entity.TokenInfo: + tokenInfo
core.webapi.holder.SingletonWebHolder *-- core.webapi.entity.TokenInfo: + tokenInfo
core.webapi.holder.SingletonWebHolder *-- core.webapi.entity.ClientSecret: + clientSecret

core.webapi.impl.GoogleSignInApiImpl -- core.webapi.holder.SingletonWebHolder
core.webapi.impl.GoogleOAuth2ApiImpl -- core.webapi.holder.SingletonWebHolder
core.webapi.impl.PhotosLibraryApiImpl -- core.webapi.holder.SingletonWebHolder

core.webapi.GoogleSignInApi <|.. core.webapi.impl.GoogleSignInApiImpl
core.webapi.GoogleOAuth2Api <|.. core.webapi.impl.GoogleOAuth2ApiImpl
core.webapi.PhotosLibraryApi <|.. core.webapi.impl.PhotosLibraryApiImpl

app.module.GoogleWebApis o-- core.webapi.GoogleSignInApi: - googleSignInApi
app.module.GoogleWebApis o-- core.webapi.GoogleOAuth2Api: - googleOAuth2Api
app.module.GoogleWebApis o-- core.webapi.PhotosLibraryApi: - photosLibraryApi
@enduml