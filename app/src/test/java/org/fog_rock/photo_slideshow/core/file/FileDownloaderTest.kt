package org.fog_rock.photo_slideshow.core.file

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.net.URL

@RunWith(AndroidJUnit4::class)
class FileDownloaderTest {

    companion object {
        private const val DOWNLOAD_URL = "https://publicobject.com/helloworld.txt"
        private const val OUTPUT_FILE_NAME = "sample.txt"
        private const val TIMEOUT_MILLISECS = 10000L
    }

    private lateinit var context: Context
    private lateinit var outputFile: File
    private lateinit var fileDownloader: FileDownloader

    @Before
    fun set() {
        context = ApplicationProvider.getApplicationContext()
        outputFile = File(context.filesDir, OUTPUT_FILE_NAME)

        fileDownloader = FileDownloader(
            TIMEOUT_MILLISECS, TIMEOUT_MILLISECS, TIMEOUT_MILLISECS,
            object : FileDownloader.Callback {
                override fun downloadResult(resultOutputFile: File?) {
                    println("Download Result.")
                    assertEquals(outputFile, resultOutputFile)
                }
            })
    }

    @Test
    fun doDownload() {
        println("Start doDownload.")
        val downloadURL = URL(DOWNLOAD_URL)
        fileDownloader.doDownload(downloadURL, outputFile)
        runBlocking {
            println("Wait $TIMEOUT_MILLISECS millisecs.")
            delay(TIMEOUT_MILLISECS)
        }
        println("End doDownload.")
    }
}