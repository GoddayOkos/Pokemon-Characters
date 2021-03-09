package dev.decagon.godday.pokemoncharacters.network

import android.os.Handler
import android.os.Looper
import dev.decagon.godday.pokemoncharacters.CURRENT_BUFFER_SIZE
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream


/**
 * This class handles the logic of preparing images for upload to the server
 * as well as keeping track of its progress using a progress bar.
 * This class serves as a custom RequestBody used by the MultiPart UploadPictureApiService in
 * interacting with the backend server.
 */

data class UploadRequestBody(
        val file: File,
        val contentType: String,
        val callback: UploadCallback) : RequestBody() {


    // interface for progress bar update
    interface UploadCallback {
        fun onProgressUpdate(percentage: Int)
    }

    // Inner class for background job
    inner class ProgressUpdate(private val uploaded: Long, private val total: Long) : Runnable {

        override fun run() {
            callback.onProgressUpdate((100 * uploaded / total).toInt())
        }

    }

    // Implementation of the members of RequestBody()

    override fun contentType(): MediaType? = MediaType.parse("$contentType/jpg")

    override fun contentLength() = file.length()

    override fun writeTo(sink: BufferedSink) {
        val length = file.length()
        val buffer = ByteArray(CURRENT_BUFFER_SIZE)
        val fileInputStream = FileInputStream(file)
        var uploaded = 0L

        fileInputStream.use { inputStream ->
            var read: Int
            val handler = Handler(Looper.getMainLooper())

            while (inputStream.read(buffer).also { read = it } != -1) {
                handler.post(ProgressUpdate(uploaded, length))
                uploaded += read
                sink.write(buffer, 0, read)
            }
        }
    }

}