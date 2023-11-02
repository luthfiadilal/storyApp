package com.example.storyapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.adapter.LoadingStateAdapter
import com.example.storyapp.adapter.StoryListAdapter
import com.example.storyapp.data.pref.PreferenceManager
import com.example.storyapp.data.pref.dataStore
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.model.MainViewModel
import com.example.storyapp.model.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var viewModel: MainViewModel

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(this))[MainViewModel::class.java]


        binding.fab.setOnClickListener {
            val intent = Intent(this, AddNewsActivity::class.java)
            startActivity(intent)
        }

        preferenceManager = PreferenceManager.getInstance(this.dataStore)

        getStories()

        CoroutineScope(Dispatchers.Main).launch {
            setUpToolbar()
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Logout Confirmation")
                    builder.setMessage("Are you sure you want to log out?")
                    builder.setPositiveButton("Yes") { _, _ ->
                        // User mengonfirmasi logout
                        CoroutineScope(Dispatchers.IO).launch {
                            preferenceManager.logout()
                        }


                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    builder.setNegativeButton("No") { dialog, _ ->
                        // User membatalkan logout
                        dialog.dismiss()
                    }
                    val dialog = builder.create()
                    dialog.show()


                    true
                }

                R.id.action_map -> {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> {
                    super.onOptionsItemSelected(menuItem)


                }
            }

        }

    }

    @SuppressLint("StringFormatInvalid")
    private suspend fun setUpToolbar() {
        preferenceManager.getUsername.collect {name ->
            binding.toolbar.title = name
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        moveTaskToBack(true)
        super.onBackPressed()
    }

    private fun getStories(){
        val adapter = StoryListAdapter()
        binding.rvStory.layoutManager = LinearLayoutManager(this)
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        viewModel.story.observe(this) {
            adapter.submitData(lifecycle, it)
        }


    }

}

