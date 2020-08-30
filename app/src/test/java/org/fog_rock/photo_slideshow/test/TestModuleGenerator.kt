package org.fog_rock.photo_slideshow.test

import com.google.photos.types.proto.MediaItem
import com.google.photos.types.proto.MediaMetadata
import com.google.photos.types.proto.Photo
import com.google.photos.types.proto.Video

class TestModuleGenerator {

    enum class Type {
        NONE,
        PHOTO,
        VIDEO,
    }

    companion object {

        fun mediaItem(
            _filename: String,
            _baseUrl: String,
            _metadata: MediaMetadata?
        ): MediaItem = MediaItem.newBuilder().apply {
            filename = _filename
            baseUrl = _baseUrl
            if (_metadata != null) mediaMetadata = _metadata
        }.build()

        fun mediaItem(
            _filename: String,
            _baseUrl: String,
            _type: Type,
            _width: Long,
            _height: Long
        ): MediaItem = mediaItem(
            _filename,
            _baseUrl,
            metadata(_type, _width, _height)
        )

        private fun metadata(
            _type: Type,
            _width: Long,
            _height: Long
        ): MediaMetadata? = when (_type) {
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
    }
}