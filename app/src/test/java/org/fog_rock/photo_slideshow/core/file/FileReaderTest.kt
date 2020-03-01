package org.fog_rock.photo_slideshow.core.file

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FileReaderTest {

    companion object {
        private const val ASSETS_FILE_NAME = "sample.txt"
        private const val EXPECTED_FILE_CONTENTS = "sample"
    }

    private lateinit var context: Context
    private lateinit var fileReader: FileReader

    @Before
    fun set() {
        context = ApplicationProvider.getApplicationContext()
        fileReader = FileReader(context)
    }

    @Test
    fun readAssetsFile() {
        assertEquals(EXPECTED_FILE_CONTENTS, fileReader.readAssetsFile(ASSETS_FILE_NAME))
    }
}