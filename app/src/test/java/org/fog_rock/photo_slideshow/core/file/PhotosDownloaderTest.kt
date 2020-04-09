package org.fog_rock.photo_slideshow.core.file

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.fog_rock.photo_slideshow.core.file.impl.PhotosDownloaderImpl
import org.fog_rock.photo_slideshow.core.webapi.PhotosGenerator
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.net.URL

class PhotosDownloaderTest {

    companion object {
        private const val WIDTH = 500L
        private const val HEIGHT = 1000L
    }

    @Rule
    @JvmField
    val tempFolder: TemporaryFolder = TemporaryFolder()

    private val photosDownloader: PhotosDownloader = PhotosDownloaderImpl(
        object : FileDownloader {
            override suspend fun requestDownload(downloadUrl: URL, outputFile: File): Boolean {
                // テストではダウンロード処理に 100 millisecs かかったと仮定する.
                delay(100)
                // テストでは PNG 拡張子だけダウンロード処理に成功したと仮定する.
                return outputFile.extension.toLowerCase() == "png"
            }
        }, WIDTH, HEIGHT
    )

    @Test
    fun requestDownloads() {

        val mediaItems = listOf(
            // 正常系.
            PhotosGenerator.generateMediaItem("sample1.png", "https://example.com/example", PhotosGenerator.Type.PHOTO, WIDTH, HEIGHT),
            // 正常系.
            PhotosGenerator.generateMediaItem("sample2.jpg",  "https://example.com/example", PhotosGenerator.Type.PHOTO, WIDTH, HEIGHT),
            // 正常系.
            PhotosGenerator.generateMediaItem("sample3.PNG", "https://example.com/example", PhotosGenerator.Type.PHOTO, WIDTH, HEIGHT),
            // 正常系.
            PhotosGenerator.generateMediaItem("sample4.jpeg",  "https://example.com/example", PhotosGenerator.Type.PHOTO, WIDTH, HEIGHT),
            // 異常系: メタデータをもたない.
            PhotosGenerator.generateMediaItem("sample5.txt", "https://example.com/example", null),
            // 異常系: データタイプがビデオ.
            PhotosGenerator.generateMediaItem("sample6.mp4", "https://example.com/example", PhotosGenerator.Type.VIDEO, WIDTH, HEIGHT),
            // 異常系: URLフォーマットが不正.
            PhotosGenerator.generateMediaItem("sample7.jpg", "example.com/example", PhotosGenerator.Type.PHOTO, WIDTH, HEIGHT)
        )

        // 正常系
        val outputFiles1 = runBlocking {
            photosDownloader.requestDownloads(mediaItems, tempFolder.root)
        }
        println("outputFiles1: $outputFiles1")
        assertEquals(2, outputFiles1.size)
        assertEquals(true, outputFiles1[0].endsWith("sample1.png"))
        assertEquals(true, outputFiles1[1].endsWith("sample3.PNG"))

        // 異常系: 出力先が存在しないディレクトリ.
        val notFoundDir = File(tempFolder.root, "notFoundDir")
        val outputFiles2 = runBlocking {
            photosDownloader.requestDownloads(mediaItems, notFoundDir)
        }
        println("outputFiles2: $outputFiles2")
        assertEquals(0, outputFiles2.size)

        // 異常系: 出力先がディレクトリではなくファイル.
        val tmpFile = File(tempFolder.root, "tmp.txt")
        tmpFile.createNewFile()
        val outputFiles3 = runBlocking {
            photosDownloader.requestDownloads(mediaItems, tmpFile)
        }
        println("outputFiles3: $outputFiles3")
        assertEquals(0, outputFiles3.size)
    }
}