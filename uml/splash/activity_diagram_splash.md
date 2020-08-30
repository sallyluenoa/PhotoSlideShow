@startuml
start
:Show app logo 2 secs;
(A)
:RequestSignIn;

partition RuntimePermission {
  if (IsGrantedRuntimePermissions?) then (YES)
  else (NO)
    :StartRuntimePermissions;

    if (IsGrantedRuntimePermissions?) then (YES)
    else (NO)
      (E)
      detach
    endif
  endif
}

partition GoogleSignIn {
  :RequestGoogleSilentSignIn;

  if (result==ApiResult.SUCCEEDED) then (YES)
  elseif (result==ApiResult.INVALID) then (YES)
    :StartGoogleSignInActivity;

    if (IsSucceededGoogleUserSignIn?) then (NO)
      (E)
      detach
    endif
  else
    (E)
    detach
  endif
}

partition UpdateUserInfo {
  :RequestUpdateUserInfo;

  if (IsSucceeded?) then (NO)
    (E)
    detach
  endif
}

:StartMainActivity;
end

partition Error {
  (E)
  :Show error dialog;
  if (Retry sign in?) then (YES)
    (A)
  else (NO)
    end
  endif
}
@enduml