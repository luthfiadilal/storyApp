package com.example.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.model.RegistrationViewModel
import com.example.storyapp.model.RegistrationViewModelFactory
import com.example.storyapp.model.ViewModelFactory
import retrofit2.HttpException

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[RegistrationViewModel::class.java]

        binding.btRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            viewModel.isLoading.observe(this) { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }


            Log.d("RegisterActivity", "Before registrasi: $name, $email, $password")
            viewModel.register(name, email, password)
            Log.d("RegisterActivity", "After registrasi")
            viewModel.registration.observe(this) { response ->
                if (response.error == false) {
                    Log.d("RegisterActivity", "onCreate: ${response.message}")
                    binding.edRegisterName.text?.clear()
                    binding.edRegisterEmail.text?.clear()
                    binding.edRegisterPassword.text?.clear()

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Register Success")
                    builder.setMessage("Registrasi berhasil")
                    builder.setPositiveButton("OK") { _, _ ->
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    val dialog = builder.create()
                    dialog.show()
                }
            }

        }

        viewModel.isRegist.observe(this) { response ->
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Register Failed")
            builder.setMessage(response)
            builder.setNegativeButton("OK") { _, _ ->

            }
            val dialog = builder.create()
            dialog.show()

        }
    }

    @SuppressLint("Recycle")
    private fun playAnimation() {
        val heading = ObjectAnimator.ofFloat(binding.heading, View.ALPHA, 1F).setDuration(600)
        val tvName = ObjectAnimator.ofFloat(binding.tvName, View.ALPHA, 1F).setDuration(600)
        val nameTextLayout =
            ObjectAnimator.ofFloat(binding.namaEditTextLayout, View.ALPHA, 1F).setDuration(600)
        val tvEmail = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1F).setDuration(600)
        val emailTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(600)
        val tvPassword = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1F).setDuration(600)
        val passwordTextLayout =
            ObjectAnimator.ofFloat(binding.passwordTextLayout, View.ALPHA, 1F).setDuration(600)
        val btRegister = ObjectAnimator.ofFloat(binding.btRegister, View.ALPHA, 1F).setDuration(600)

        AnimatorSet().apply {
            playSequentially(
                heading,
                tvName,
                nameTextLayout,
                tvEmail,
                emailTextLayout,
                tvPassword,
                passwordTextLayout,
                btRegister
            )
            start()
        }
    }


}