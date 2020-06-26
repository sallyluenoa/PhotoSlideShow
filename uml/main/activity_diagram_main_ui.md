@startuml
start

:Show Progress;
:Request background task;

repeat
  :load MediaItem from DB;
  
  if (mediaItems.isEmpty()) then (YES)
    stop
  endif

  repeat
    :Show MediaItem image;
    :Wait a few secs;

    if (LastIndex?) then (YES)
      :Request background task;
    endif

    backward:Next Index;
  repeat while (No received DB update by background thread)

repeat while
@enduml
