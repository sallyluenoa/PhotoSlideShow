@startuml
start
:Show app logo 2 secs;

(A)
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
  if (IsSignedInAccount?) then (YES)
    :RequestSilentSignIn;

    if (Is succeeded?) then (YES)
    else (NO)
      (E)
      detach
    endif

  else (NO)
    :StartGoogleSignInActivity;

    if (Is succeeded?) then (YES)
    else (NO)
      (E)
      detach
    endif

  endif
}

partition UpdateUserInfo {
  :getSignedInAccount;

  if (Is email contained in account?) then (YES)
    :UserInfoDatabase find;

    if (Is userInfo found?) then (YES)
      :RequestTokenInfoWithRefreshToken;
    endif

    if (Is succeeded to get tokenInfo?) then (NO)
      :RequestTokenInfoWithAuthCode;
    endif

    if (Is succeeded to get tokenInfo?) then (YES)
      :UserInfoDatabase update;
    endif
  endif

  if (Is succeeded to update database?) then (NO)
    :RevokeAccess;
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
