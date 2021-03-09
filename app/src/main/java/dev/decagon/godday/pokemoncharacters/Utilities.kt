package dev.decagon.godday.pokemoncharacters

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * This file contains constants, methods and utilities used in some part
 * of this app.
 */

internal const val BASE_URL = "https://pokeapi.co/api/v2/pokemon/"

internal const val BASE_URL_UPLOAD = "https://darot-image-upload-service.herokuapp.com/api/v1/"

internal const val IMAGE_URL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"

internal const val CURRENT_BUFFER_SIZE = 1048

internal const val DEFAULT_POKEMON_SIZE = 1118

internal val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

internal val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .build()

internal val uploadRetrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL_UPLOAD)
        .build()

// This method is used to bind image to image views. it uses
// Glide library.
internal fun bindImage(imgUrl: String?, imgView: ImageView) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
                .load(imgUri).apply(RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image))
                .into(imgView)
    }
}

// This method is used in extracting the index out of a url string
// it returns the number before the last / in the url
internal fun getIndexFromUrl(url: String): String {
    val index = url.split("/")
    val int = index.lastIndex
    return index.elementAt(int - 1)
}

// This method is used in converting user input to string
// with provisions for error handling
internal fun doneButtonAction(context: Context, input: String): Int {
    if (input == "0") {
        Toast.makeText(context, "Enter a number greater than 0 else you'll get 20 items", Toast.LENGTH_LONG)
                .show()
    }
    return try {
        input.toInt()
    } catch (e: NumberFormatException) {
        DEFAULT_POKEMON_SIZE
    }
}

// Extension function
internal fun View.snackbar(message: String) {
    Snackbar.make(
            this, message, Snackbar.LENGTH_LONG
    ).also { snackbar ->
        snackbar.setAction("Ok") {
            snackbar.dismiss()
        }

    }.show()
}

// Extension function
internal fun ContentResolver.getFileName(uri: Uri): String {
    var name = ""
    var cursor = query(uri, null, null, null, null)
    cursor?.use {
        it.moveToFirst()
        name = cursor.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
    }
    return name
}