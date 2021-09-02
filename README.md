# PhotoSlideShow

## Overview
* Googleフォトで共有したアルバム内の写真を表示するアプリです。
* アルバム内の写真をランダムピックアップしてスライドショーとして表示します。
* Googleフォト連携機能を利用しているため、フォトの共有にアップロードされた新しい写真も自動で適宜取得して表示することが可能です。

## How to use
* アプリを初回起動するとスプラッシュ後にパーミッション許可の確認画面が表示されるので許可してください。（Android 6 未満はパーミッション許可がでません。）
* Googleアカウントサインイン画面がでるので、サインインしてください。（大抵はAndroid端末でサインインしているGoogleアカウントに選択することになると思います。）
* 初回サインインの場合、Googleアカウントへのアクセスをダイアログの案内に沿って許可してください。（許可するとG-mail経由でセキュリティ通知が投げられる場合があります。）
* サインインしてしばらくすると、フォトの共有内にあるアルバムの一覧が表示されます。アプリでスライドショーしたいアルバムを選択してください。
* しばらくして読み込み完了すると画像が表示されます。

## Tools
* Android Studio 最新版

## Difference between Old Project
旧プロジェクト([PhotoSlideShow_Java](https://github.com/sallyluenoa/PhotoSlideShow_Java))との違い

* 全体
  * アーキテクチャ: 特になし → VIPER
  * 言語: Java → Kotlin
* 実装方法
  * 非同期処理: AsyncTask → Kotlin Coroutine
  * ダウンロード: HttpURLConnection → OKHttp
* Google APIs
  * サインイン方式: GoogleApiClient → GoogleSignInClient
  * アクセストークン取得: GoogleAuthUtil → GoogleAuthorizationCodeTokenRequest

## Project Framework

```
org.fog_rock.photo_slideshow
  ├ app: アプリケーションに関するモジュール
  │  ├ main: スライドメイン画面
  │  ├ menu: メニュー画面
  │  ├ module: ライブラリモジュール
  │  ｜  ├ lib: coreに関連するライブラリ
  │  ｜  └ ui: UIに関連するライブラリ
  │  ├ select: アルバム選択画面
  │  └ splash: 起動画面
  └ core: ライブラリやインターフェース等のサポートモジュール
     ├ database: データベース関連の処理
     ├ extension: 拡張関数定義
     ├ file: ファイル関連の処理
     ├ math: Mathライブラリを活用した処理
     ├ viper: VIPERモジュールインターフェース
     └ webapi: Web API 関連
```
