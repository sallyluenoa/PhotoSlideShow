package org.fog_rock.photo_slideshow.core.file

import androidx.test.platform.app.InstrumentationRegistry
import org.fog_rock.photo_slideshow.core.file.impl.AssetsFileReaderImpl
import org.junit.Assert.assertEquals
import org.junit.Test

class AssetsFileReaderTest {

    private val context = InstrumentationRegistry.getInstrumentation().context

    private val fileReader = AssetsFileReaderImpl(context)

    @Test
    fun read() {

        // 正常系
        assertEquals(
            "sample",
            fileReader.read("sample.txt")
        )

        // 異常系 (ファイルが存在しない)
        assertEquals(
            null,
            fileReader.read("notFound.txt")
        )
    }
}