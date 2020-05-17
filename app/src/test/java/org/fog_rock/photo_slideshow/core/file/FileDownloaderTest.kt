package org.fog_rock.photo_slideshow.core.file

import kotlinx.coroutines.runBlocking
import org.fog_rock.photo_slideshow.core.file.impl.FileDownloaderImpl
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.net.URL

class FileDownloaderTest {

    @Rule
    @JvmField
    val tempFolder: TemporaryFolder = TemporaryFolder()

    private val fileDownloader: FileDownloader = FileDownloaderImpl(10000L)

    @Test
    fun requestDownload() {

        val downloadURL = URL("https://publicobject.com/helloworld.txt")
        val outputFile1 = File(tempFolder.root, "sample1.txt")

        // 正常系
        val normalResult = runBlocking {
            fileDownloader.requestDownload(downloadURL, outputFile1)
        }
        assertEquals(true, normalResult)

        // すでにファイルが存在
        val duplicateResult = runBlocking {
            fileDownloader.requestDownload(downloadURL, outputFile1)
        }
        assertEquals(true, duplicateResult)

        val notFoundURL = URL("https://publicobject.com/helloworld")
        val outputFile2 = File(tempFolder.root, "sample2.txt")

        // 異常系 (404: Not Found)
        val errorNotFoundResult = runBlocking {
            fileDownloader.requestDownload(notFoundURL, outputFile2)
        }
        assertEquals(false, errorNotFoundResult)
    }
}