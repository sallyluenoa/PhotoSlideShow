@startuml
start

:Show Progress;

repeat
  :requestLoadDisplayedPhotos;

  if (displayedPhotos.isEmpty()) then (YES)
    stop
  endif

  repeat
    if (Is Index 0?) then (YES)
      :requestUpdateDisplayedPhotos;
    endif

    :Displayed photos;
    :Wait a moment...;

    backward:Next Index;
  repeat while (No notified update photos?)

repeat while
@enduml
