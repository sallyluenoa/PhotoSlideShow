package org.fog_rock.photo_slideshow.core.webapi

import com.google.photos.types.proto.MediaItem
import com.google.photos.types.proto.MediaMetadata
import com.google.photos.types.proto.Photo
import com.google.photos.types.proto.Video

class PhotosGenerator {

    enum class Type {
        NONE,
        PHOTO,
        VIDEO,
    }

    companion object {

        fun generateMediaItem(
            _filename: String,
            _baseUrl: String,
            _metadata: MediaMetadata?
        ): MediaItem = MediaItem.newBuilder().apply {
            filename = _filename
            baseUrl = _baseUrl
            if (_metadata != null) mediaMetadata = _metadata
        }.build()

        fun generateMediaItem(
            _filename: String,
            _baseUrl: String,
            _type: Type,
            _width: Long,
            _height: Long
        ): MediaItem {
            val metadata = when (_type) {
                Type.PHOTO -> MediaMetadata.newBuilder().apply {
                    photo = Photo.newBuilder().build()
                    width = _width
                    height = _height
                }.build()
                Type.VIDEO -> MediaMetadata.newBuilder().apply {
                    video = Video.newBuilder().build()
                    width = _width
                    height = _height
                }.build()
                Type.NONE -> null
            }
            return generateMediaItem(_filename, _baseUrl, metadata)
        }
    }
}