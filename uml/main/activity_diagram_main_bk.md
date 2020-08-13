@startuml
:requestUpdateDisplayedPhotos;

if (isNeededUpdatePhotos) then (NO)
  stop
endif

partition DecideAlbum {
  if (hasSelectedAlbums) then (NO)
    :startSelectActivity;
    :onActivityResult;

    if (Is selected album found?) then (NO)
      stop
    endif
  endif
}

partition DownloadImages {
  while (selectedAlbums.foreach)
    :PhotosLibraryApi#requestMediaItems;
    :Make random photo MediaItems;
    :PhotosDownloader#requestMediaItems;
  end while
}

partition UpdateDatabase {
  :Database#replaceSelectedData;
  :Database#findUserInfoData;
}

:requestUpdatePhotosResult;
stop
@enduml
