package org.fog_rock.photo_slideshow.core.webapi.entity

/**
 * client_secret.json から取得される情報を格納するデータクラス.
 */
data class ClientSecret(val web: WebInfo) {

    data class WebInfo(
        val clientId: String,
        val projectId: String,
        val authUri: String,
        val tokenUri: String,
        val authProviderX509CertUrl: String,
        val clientSecret: String,
        val redirectUris: List<String>,
        val javascriptOrigins: List<String>
    )
}