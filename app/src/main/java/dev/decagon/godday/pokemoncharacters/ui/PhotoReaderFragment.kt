package dev.decagon.godday.pokemoncharacters.ui

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dev.decagon.godday.pokemoncharacters.R
import dev.decagon.godday.pokemoncharacters.databinding.FragmentPhotoReaderBinding
import dev.decagon.godday.pokemoncharacters.getFileName
import dev.decagon.godday.pokemoncharacters.models.ServerResponse
import dev.decagon.godday.pokemoncharacters.network.UploadPictureApiService
import dev.decagon.godday.pokemoncharacters.network.UploadRequestBody
import dev.decagon.godday.pokemoncharacters.snackbar
import kotlinx.android.synthetic.main.fragment_photo_reader.*
import okhttp3.MediaType
//import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * This fragment requires permission to read phone's storage
 * This is to enable the user select an image which will be uploaded to the
 * backend server.
 */

class PhotoReaderFragment : Fragment(), UploadRequestBody.UploadCallback {
    private var _binding: FragmentPhotoReaderBinding? = null
    private val binding get() = _binding!!
    private lateinit var addButton: FloatingActionButton
    private lateinit var message: TextView
    private lateinit var picture: ImageView
    private lateinit var uploadBtn: Button
    private var imageUri: Uri? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhotoReaderBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        /**
         * Once this fragment is launched, checks if the permission to read phone's contact
         * has been granted. If it has been granted, fine. Else request for the permission
         */
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
            )
        }

        addButton = binding.fab
        message = binding.message
        picture = binding.image
        uploadBtn = binding.upload

        // Upload the image when this button is clicked
        uploadBtn.setOnClickListener {
            success_message.visibility = View.GONE
            uploadImage()
        }

        // This button is use to request for the permission to read contact if it was once denied
        addButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    ) !=
                    PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        1
                )
            } else {
                handlePermission()
            }
        }
    }

    /**
     * This method handles the whole logic of uploading image to the server,
     * updating the progress bar as well as providing feedback from the server
     * about the status of the upload
     */

    private fun uploadImage() {
        if (imageUri == null) {
            photo_frag.snackbar("Select an image to upload")
            return
        }

        val parcelFileDescriptor = requireActivity().contentResolver?.openFileDescriptor(imageUri!!,
                "r", null) ?: return

        val file = File(requireActivity().cacheDir, requireActivity().contentResolver.getFileName(imageUri!!))
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        progress_bar.progress = 0
        val body = UploadRequestBody(file, "image", this)

        UploadPictureApiService.UploadApi.retrofitService.postImageToServer(
                MultipartBody.Part.createFormData("file", file.name, body),
                RequestBody.create(MediaType.parse("multipart/form-data"), "This image was uploaded by Godday")
        ).enqueue(object : Callback<ServerResponse> {

            override fun onResponse(call: Call<ServerResponse>, response: Response<ServerResponse>) {
                if (response.isSuccessful){
                    Log.d("response", "output_success:${response.body().toString()}")
                    progress_bar.progress = 100
                    success_message.text = response.body()?.message.toString()
                    success_message.visibility = View.VISIBLE
                } else {
                    Log.d("response", "output is:$response")
                }


            }

            override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                success_message.text = t.message
                success_message.visibility = View.VISIBLE
            }

        })
    }

    override fun onProgressUpdate(percentage: Int) {
        progress_bar.progress = percentage
    }

    /**
     *  If the user granted the permission to read phone's
     *  storage, continue and read the storage, else if the
     *  user denied the permission, display an error message on the screen
     */

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                handlePermission()
            } else {
                message.visibility = View.VISIBLE
            }
        }
    }

    // Handles the actions to be performed when the permission to read read contact has been granted
    // to the app.
    private fun handlePermission() {
        message.visibility = View.GONE
        readPhoto()
    }

    // Intent to read phone storage and pick an image for upload
    private fun readPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    // Once an image is selected, update the image view with the image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.data!!
            picture.setImageURI(imageUri)
        }
    }

}