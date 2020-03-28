package org.fog_rock.photo_slideshow.core.file

interface FileReader {

    /**
     * assets 以下に設置されているファイルの読み込みを行う.
     * @param fileName ファイル名
     * @return ファイルの読み込みに成功した場合はファイル内の文字列、失敗した場合はNULL
     */
    fun readAssetsFile(fileName: String): String?
}