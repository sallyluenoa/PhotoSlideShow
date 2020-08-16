package org.fog_rock.photo_slideshow.core.webapi.holder

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.OAuth2Credentials
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.photos.library.v1.PhotosLibraryClient
import com.google.photos.library.v1.PhotosLibrarySettings
import okhttp3.OkHttpClient
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.core.file.AssetsFileReader
import org.fog_rock.photo_slideshow.core.webapi.entity.ClientSecret
import org.fog_rock.photo_slideshow.core.webapi.entity.PhotoScope
import java.io.IOException
import java.util.concurrent.TimeUnit

object SingletonWebHolder {

    lateinit var clientSecret: ClientSecret

    lateinit var okHttpClient: OkHttpClient

    lateinit var googleSignInClient: GoogleSignInClient

    var photosLibraryClient: PhotosLibraryClient? = null

    fun loadClientSecret(assetsFileReader: AssetsFileReader, jsonFileName: String) {
        val jsonString = assetsFileReader.read(jsonFileName)
            ?: throw NullPointerException("Failed to read $jsonFileName")
        clientSecret = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
            .fromJson(jsonString, ClientSecret::class.java)
    }

    fun setupOkHttpClient(
        connectionTimeoutMilliSecs: Long,
        readTimeoutMilliSecs: Long,
        writeTimeoutMilliSecs: Long
    ) {
        okHttpClient = OkHttpClient.Builder().apply {
            connectTimeout(connectionTimeoutMilliSecs, TimeUnit.MILLISECONDS)
            readTimeout(readTimeoutMilliSecs, TimeUnit.MILLISECONDS)
            writeTimeout(writeTimeoutMilliSecs, TimeUnit.MILLISECONDS)
        }.build()
    }

    fun setupGoogleSignInClient(
        context: Context,
        scopes: List<PhotoScope>,
        requestIdToken: Boolean = false,
        requestServerAuthCode: Boolean = false
    ) {
        googleSignInClient = try {
            val clientId = context.getString(R.string.default_web_client_id)
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).apply {
                requestEmail()
                scopes.forEach { requestScopes(it.scope()) }
                if (requestIdToken) requestIdToken(clientId)
                if (requestServerAuthCode) requestServerAuthCode(clientId)
            }.build()
            GoogleSignIn.getClient(context, options)
        } catch (e: NullPointerException) {
            throw NullPointerException("Failed to setup GoogleSignInClient.")
        }
    }

    fun updatePhotosLibraryClient(accessToken: String) {
        photosLibraryClient = try {
            val credentials = OAuth2Credentials.create(AccessToken(accessToken, null))
            val settings = PhotosLibrarySettings.newBuilder().apply {
                credentialsProvider = FixedCredentialsProvider.create(credentials)
            }.build()
            PhotosLibraryClient.initialize(settings)
        } catch (e : IOException) {
            throw IOException("Failed to update PhotosLibraryClient.")
        }
    }

    fun photosLibraryClient(): PhotosLibraryClient =
        photosLibraryClient ?: throw NullPointerException("PhotosLibraryClient is null.")
}