@startuml
:requestUpdateDisplayedPhotos;

if (isNeededUpdatePhotos) then (NO)
  stop
endif

partition DecideAlbum {
  if (hasSelectedAlbums) then (NO)
    :requestSharedAlbums;
    :startSelectActivity;
    :onActivityResult;

    if (resultCode == OK) then (NO)
      stop
    endif

    :updateSelectedAlbums;
  endif
}

partition DownloadImages_and_UpdateDB {
  while (selectedAlbums.foreach)
    :requestMediaItemsFromAlbumId;
    :Make random photo MediaItems;
    :requestDownloadMediaItems;
    :updateDisplayedPhotos;
  end while
}

:notifyUpdatedPhotoData;
stop
@enduml
