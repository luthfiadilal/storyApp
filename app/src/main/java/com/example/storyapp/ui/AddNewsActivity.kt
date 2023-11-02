package com.example.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.network.ApiConfig
import com.example.storyapp.data.pref.PreferenceManager
import com.example.storyapp.data.pref.dataStore
import com.example.storyapp.data.remote.UserRepository
import com.example.storyapp.databinding.ActivityAddNewsBinding
import com.example.storyapp.model.AddNewsViewModel
import com.example.storyapp.model.AddNewsViewModelFactory
import com.example.storyapp.model.ViewModelFactory
import com.example.storyapp.utils.getImageUri
import com.example.storyapp.utils.reduceFileImage
import com.example.storyapp.utils.uriToFile
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import kotlin.math.log

class AddNewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewsBinding
    private lateinit var addNewsViewModel: AddNewsViewModel

    private var currentImageUri: Uri? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()


        addNewsViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(this))[AddNewsViewModel::class.java]

        binding.btGallery.setOnClickListener {
            startGallery()
        }
        binding.btCamera.setOnClickListener {
            startCamera()
        }

        addNewsViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        binding.buttonAdd.setOnClickListener {
            currentImageUri?.let { uri ->
                val imageFile = uriToFile(uri, this)
                val reduceFile = reduceFileImage(imageFile)
                val description = binding.edAddDescription.text.toString()

                val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())

                val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    reduceFile.name,
                    requestImageFile

                )

                addNewsViewModel.getSession().observe(this) { user ->
                    if (user.isLogin) {
                        val token = user.token
                        addNewsViewModel.addStory(token, imageMultiPart, descriptionRequestBody)
                        addNewsViewModel.uploadResponse.observe(this) { response ->
                            if (response.error == false) {
                                val builder = AlertDialog.Builder(this@AddNewsActivity)
                                builder.setTitle("Upload Success")
                                builder.setMessage("Story berhasil diunggah")
                                builder.setPositiveButton("Lanjut") { _, _ ->
                                    // User menonfirmasi login berhasil
                                    val intent = Intent(this@AddNewsActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()

                                }
                                val dialog = builder.create()
                                dialog.show()

                            }
                        }
                    }
                }
            }
        }
        addNewsViewModel.isUpload.observe(this) { response ->
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Upload Failed")
            builder.setMessage(response)
            builder.setPositiveButton("Oke") {_,_ ->

            }
            val dialog = builder.create()
            dialog.show()

        }
    }

    private fun startCamera(){
        currentImageUri = getImageUri(this)
        launchCamera.launch(currentImageUri)
    }

    private val launchCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ){ isSucces ->
        if (isSucces){
            showImage()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.tvImageView.setImageURI(it)
        }
    }

    private fun playAnimation(){
        val appBarLayout = ObjectAnimator.ofFloat(binding.appBarLayout, View.ALPHA, 1F).setDuration(600)
        val tvImage = ObjectAnimator.ofFloat(binding.tvImageView, View.ALPHA, 1F).setDuration(600)
        val btGallery = ObjectAnimator.ofFloat(binding.btGallery, View.ALPHA, 1F).setDuration(600)
        val btCamera = ObjectAnimator.ofFloat(binding.btCamera, View.ALPHA, 1F).setDuration(600)
        val textLayout = ObjectAnimator.ofFloat(binding.editTextMultiLine, View.ALPHA, 1F).setDuration(600)
        val buttonAdd = ObjectAnimator.ofFloat(binding.buttonAdd, View.ALPHA, 1F).setDuration(600)


        AnimatorSet().apply {
            playSequentially(appBarLayout, tvImage, btGallery, btCamera, textLayout, buttonAdd)
            start()
        }
    }


}