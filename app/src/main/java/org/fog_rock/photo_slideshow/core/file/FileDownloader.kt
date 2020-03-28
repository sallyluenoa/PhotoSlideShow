package org.fog_rock.photo_slideshow.core.file

import java.io.File
import java.net.URL

interface FileDownloader {

    /**
     * ファイルダウンロードを行う.
     * コルーチン内で呼び出すこと.
     * @param downloadUrl ダウンロードURL
     * @param outputFile 出力先ファイル
     * @return ダウンロード成功やすでに存在などによりファイルが存在する場合はtrue、それ以外はfalse
     */
    suspend fun requestDownload(downloadUrl: URL, outputFile: File): Boolean
}