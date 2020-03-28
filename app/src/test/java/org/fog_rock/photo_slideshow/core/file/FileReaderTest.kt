package org.fog_rock.photo_slideshow.core.file

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.fog_rock.photo_slideshow.core.file.impl.FileReaderImpl
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FileReaderTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val fileReader: FileReader = FileReaderImpl(context)

    @Test
    fun readAssetsFile() {

        assertEquals(
            "sample",
            fileReader.readAssetsFile("sample.txt")
        )

        assertEquals(
            null,
            fileReader.readAssetsFile("notFound.txt")
        )
    }
}