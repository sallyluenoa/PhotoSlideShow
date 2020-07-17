package org.fog_rock.photo_slideshow.app.main.entity

enum class UpdatePhotosRequest(val code: Int) {

    SKIPPED(0),

    LOAD_USER_INFO(1000),

    UPDATE_REFRESH_TOKEN(1001),

    SELECT_ALBUMS(1010),

    DOWNLOAD_PHOTOS(1100),

    COMPLETED(1111),

    UNKNOWN(9999);
}