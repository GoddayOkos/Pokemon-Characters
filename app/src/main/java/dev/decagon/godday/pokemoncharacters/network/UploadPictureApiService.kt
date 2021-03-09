package dev.decagon.godday.pokemoncharacters.network

import dev.decagon.godday.pokemoncharacters.models.ServerResponse
import dev.decagon.godday.pokemoncharacters.uploadRetrofit
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * This interface provides the services of posting images to the backend API (server)
 * This interface provides all the network services used by the photo reader fragment.
 * In addition, this interface uses the singleton design pattern making sure, that only one
 * instance of it is available throughout the own application.
 */

internal interface UploadPictureApiService {

    @Multipart
    @POST("upload")
    fun postImageToServer(@Part image: MultipartBody.Part,
    @Part("mutlipart/form-data") body: RequestBody): Call<ServerResponse>

    object UploadApi {
        val retrofitService: UploadPictureApiService by lazy {
            uploadRetrofit.create(UploadPictureApiService::class.java)
        }
    }
}

//@Part("file") file: RequestBody