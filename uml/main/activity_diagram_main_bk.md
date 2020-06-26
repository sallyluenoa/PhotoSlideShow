@startuml
:requestUpdatePhotoData;

if (Is needed photo update?) then (NO)
  stop
endif

partition UpdateAccessToken {
  if (Is expired accessToken?) then (YES)
    :requestAccessTokenWithRefreshToken;
  endif
}

partition DecideAlbum {
  if (Has selected album in DB?) then (YES)
    :requestSharedAlbums;
    :startSelectActivity;
    :ActivityResult;
  endif
}

partition DownloadImages_and_UpdateDB {
  :requestMediaItemsFromAlbumId;
  :Make random photo MediaItems;
  :Download MediaItem image files;
  :update MediaItems DB;
}

:notifyUpdatedPhotoData;
stop
@enduml
