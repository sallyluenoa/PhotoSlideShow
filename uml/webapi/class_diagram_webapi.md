@startuml
namespace core.webapi #EEFFFF {

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
      + expiredAccessTokenTimeMillis: Long
    }

    ClientSecret *-- WebInfo: web
  }

  namespace holder {
    class SingletonWebHolder << (O, gold) Singleton >> {
      + okHttpClient: OkHttpClient
      + googleSignInClient: GoogleSignInClient
      + photosLibraryClient: PhotosLibraryClient?

      + loadClientSecret(assetsFileReader: AssetsFileReader, jsonFileName: String)
      + setupOkHttpClient(connectionTimeoutMilliSecs: Long, readTimeoutMilliSecs: Long, writeTimeoutMilliSecs: Long)
      + fun setupGoogleSignInClient(context: Context, scopes: List<PhotoScope>, requestIdToken: Boolean, requestServerAuthCode: Boolean)
      + updatePhotosLibraryClient(accessToken: String)
    }
  }

  namespace impl {
    class GoogleSignInApiImpl
    class GoogleOAuth2ApiImpl
    class PhotosLibraryApiImpl
  }

  interface GoogleSignInApi {
    + requestSilentSignIn(): ApiResult
    + requestSignOut(): ApiResult
    + requestRevokeAccess(): ApiResult
    + {static} isSignedInAccount(context: Context): Boolean
    + {static} getSignedInAccount(context: Context): GoogleSignInAccount
    + {static} getSignedInEmailAddress(context: Context): String
    + {static} isSucceededUserSignIn(data: Intent?): Boolean
  }
  interface GoogleOAuth2Api {
    + requestTokenInfoWithAuthCode(serverAuthCode: String): TokenInfo?
    + requestTokenInfoWithRefreshToken(refreshToken: String): TokenInfo?
  }
  interface PhotosLibraryApi {
    + requestAlbum(albumId: String): Album
    + requestMediaItem(mediaItemId: String): MediaItem
    + requestUpdateAlbums(albums: List<Album>): List<Album>
    + requestUpdateMediaItems(mediaItems: List<MediaItem>): List<MediaItem>
    + requestSharedAlbums(): List<Album>
    + requestMediaItems(album: Album): List<MediaItem>
  }
}

core.webapi.holder.SingletonWebHolder *-- core.webapi.entity.ClientSecret: clientSecret

core.webapi.impl.GoogleSignInApiImpl -- core.webapi.holder.SingletonWebHolder
core.webapi.impl.GoogleOAuth2ApiImpl -- core.webapi.holder.SingletonWebHolder
core.webapi.impl.PhotosLibraryApiImpl -- core.webapi.holder.SingletonWebHolder

core.webapi.GoogleSignInApi <|.. core.webapi.impl.GoogleSignInApiImpl
core.webapi.GoogleOAuth2Api <|.. core.webapi.impl.GoogleOAuth2ApiImpl
core.webapi.PhotosLibraryApi <|.. core.webapi.impl.PhotosLibraryApiImpl

@enduml
