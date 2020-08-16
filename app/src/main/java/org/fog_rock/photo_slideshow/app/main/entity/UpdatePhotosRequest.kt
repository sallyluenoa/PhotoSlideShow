package org.fog_rock.photo_slideshow.app.main.entity

enum class UpdatePhotosRequest(val code: Int) {

    CONFIG_UPDATE(1000),

    SELECT_ALBUMS(1001),

    DOWNLOAD_PHOTOS(1010),

    UPDATE_DATABASE(1100),

    COMPLETED(1111),

    UNKNOWN(9999);
}