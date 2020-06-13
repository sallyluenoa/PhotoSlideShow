package org.fog_rock.photo_slideshow.core.file

import org.fog_rock.photo_slideshow.core.file.impl.AssetsFileReaderImpl
import org.fog_rock.photo_slideshow.test.AndroidTestModuleGenerator
import org.junit.Assert.assertEquals
import org.junit.Test

class AssetsFileReaderTest {

    private val fileReader = AssetsFileReaderImpl(AndroidTestModuleGenerator.testContext())

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