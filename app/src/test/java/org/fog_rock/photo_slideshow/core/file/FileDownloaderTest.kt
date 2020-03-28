package org.fog_rock.photo_slideshow.core.file

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.fog_rock.photo_slideshow.core.file.impl.FileDownloaderImpl
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.net.URL

@RunWith(AndroidJUnit4::class)
class FileDownloaderTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val fileDownloader: FileDownloader = FileDownloaderImpl(10000L)

    @Test
    fun doDownload() {

        val downloadURL = URL("https://publicobject.com/helloworld.txt")
        val outputFile1 = File(context.filesDir, "sample1.txt")

        val ret1 = runBlocking {
            fileDownloader.requestDownload(downloadURL, outputFile1)
        }
        assertEquals(true, ret1)
    }
}