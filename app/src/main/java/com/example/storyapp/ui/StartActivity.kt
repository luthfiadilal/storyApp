package com.example.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.storyapp.R
import com.example.storyapp.data.pref.PreferenceManager
import com.example.storyapp.data.pref.dataStore
import com.example.storyapp.databinding.ActivityStartBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding
    private lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = PreferenceManager.getInstance(this.dataStore)

        CoroutineScope(Dispatchers.Main).launch {
            val user = preferenceManager.getSession().first()
            if (user.isLogin) {
                val intent = Intent(this@StartActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.buttonSignIn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.buttonLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        playAnimation()
    }

    @SuppressLint("Recycle")
    private fun playAnimation(){
        val heading = ObjectAnimator.ofFloat(binding.heading, View.ALPHA, 1F).setDuration(600)
        val subtitle = ObjectAnimator.ofFloat(binding.subtitle, View.ALPHA, 1F).setDuration(600)
        val btSignIn = ObjectAnimator.ofFloat(binding.buttonSignIn, View.ALPHA, 1F).setDuration(600)
        val btLogin = ObjectAnimator.ofFloat(binding.buttonLogin, View.ALPHA, 1F).setDuration(600)

        AnimatorSet().apply {
            playSequentially(heading, subtitle, btSignIn, btLogin)
            start()
        }
    }
}