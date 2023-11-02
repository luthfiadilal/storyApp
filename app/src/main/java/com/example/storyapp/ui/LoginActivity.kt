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
import com.example.storyapp.R
import com.example.storyapp.data.network.ApiConfig
import com.example.storyapp.data.pref.PreferenceManager
import com.example.storyapp.data.pref.User
import com.example.storyapp.data.pref.dataStore
import com.example.storyapp.data.remote.UserRepository
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.model.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()

        preferenceManager = PreferenceManager.getInstance(this.dataStore)
        viewModel =
            LoginViewModel(UserRepository.getInstance(preferenceManager, ApiConfig.getApiService()))



        binding.register.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }


        binding.btLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            viewModel.isLoading.observe(this) { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }


            viewModel.login(email, password).observe(this) { response ->
                if (response.error == false) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val saveToken = async(Dispatchers.IO) {
                            preferenceManager.saveSession(
                                User(
                                    response.loginResult?.name.toString(),
                                    AUTH_KEY + (response.loginResult?.token.toString()),
                                    true
                                )
                            )
                        }

                        saveToken.await()

                        Log.d(
                            "Login Activity",
                            "response ${response.loginResult?.name} ${response.loginResult?.token}"
                        )

                    }
                    val builder = AlertDialog.Builder(this@LoginActivity)
                    builder.setTitle("Login Success")
                    builder.setMessage("Welcome ${response.loginResult?.name}")
                    builder.setPositiveButton("OK") { _, _ ->
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    }
                    val dialog = builder.create()
                    dialog.show()
                }
            }
        }

        viewModel.login.observe(this) { response ->
            CoroutineScope(Dispatchers.Main).launch {
                val builder = AlertDialog.Builder(this@LoginActivity)
                builder.setTitle("Login Failed")
                builder.setMessage(response)
                builder.setPositiveButton("OK") { _, _ ->

                }
                val dialog = builder.create()
                dialog.show()
            }
        }
    }

    @SuppressLint("Recycle")
    private fun playAnimation() {
        val heading = ObjectAnimator.ofFloat(binding.heading, View.ALPHA, 1F).setDuration(600)
        val tvEmail = ObjectAnimator.ofFloat(binding.textEmail, View.ALPHA, 1f).setDuration(600)
        val emaiTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1F).setDuration(600)
        val tvPassword =
            ObjectAnimator.ofFloat(binding.textPassword, View.ALPHA, 1F).setDuration(600)
        val passwordTextLayout =
            ObjectAnimator.ofFloat(binding.loginEditTextLayout, View.ALPHA, 1F).setDuration(600)
        val btLogin = ObjectAnimator.ofFloat(binding.btLogin, View.ALPHA, 1F).setDuration(600)
        val btRegis = ObjectAnimator.ofFloat(binding.register, View.ALPHA, 1F).setDuration(600)

        AnimatorSet().apply {
            playSequentially(
                heading,
                tvEmail,
                emaiTextLayout,
                tvPassword,
                passwordTextLayout,
                btLogin,
                btRegis
            )
            start()
        }
    }

    companion object {
        private const val AUTH_KEY = "Bearer "
    }
}