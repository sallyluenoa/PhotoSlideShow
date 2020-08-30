@startuml
start

:Show Progress;
:RequestLoadDisplayedPhotos;

repeat
  :GetDisplayedPhoto;

  if (displayedPhoto != null) then (YES)
    :PresentImage;
    :Delay X secs...;
  else
    :Delay 1 sec...;
  endif

repeat while
@enduml