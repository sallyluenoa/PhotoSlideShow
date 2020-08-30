@startuml
:RequestLoadDisplayedPhotos;
:RequestUpdateDisplayedPhotos;

if (IsNeededUpdatePhotos?) then (NO)
  stop
endif

partition DecideAlbum {
  if (HasSelectedAlbums?) then (NO)
    :StartSelectActivity;

    if (Is selected album found?) then (NO)
      stop
    endif
  endif
}

partition DownloadImages {
  :RequestDownloadPhotos;
  :RequestUpdateDatabase;
}

stop
@enduml