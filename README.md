# PhotoSlideShow

## Overview

## Tools
* Android Studio 最新版

## Difference between Old Project
旧プロジェクト([PhotoSlideShow_Java](https://github.com/sallyluenoa/PhotoSlideShow_Java))との違い

* 全体
  * アーキテクチャ: 特になし → VIPER
  * 言語: Java → Kotlin
* 実装方法
  * 非同期処理: AsyncTask → Coroutine
  * ダウンロード: HttpURLConnection → OKHttp
* Google APIs
  * サインイン方式: GoogleApiClient → GoogleSignInClient
  * アクセストークン取得: GoogleAuthUtil → GoogleAuthorizationCodeTokenRequest

## Project Framework

```
org.fog_rock.photo_slideshow
  ├ app: アプリケーションUIに関するモジュール
  │  ├ main: スライドメイン画面
  │  ├ menu: メニュー画面
  │  ├ module: UIに関連するライブラリモジュール  
  │  ├ select: アルバム選択画面
  │  └ splash: 起動画面
  └ core: ライブラリやデータ等のサポートモジュール
     ├ entity: データクラス
     ├ file: ファイル関連の処理
     ├ math: Mathライブラリを活用した処理
     ├ viper: VIPERモジュールインターフェース
     └ webapi: Web API 関連
```
