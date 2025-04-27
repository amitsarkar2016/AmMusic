package com.codetaker.ammusic.ui.main


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codetaker.ammusic.R
import com.codetaker.ammusic.databinding.ActivityMainBinding
import com.codetaker.ammusic.extensions.replaceFragment
import com.codetaker.ammusic.ui.home.HomeFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragment = HomeFragment()
        replaceFragment(fragment, R.id.mainContainer, false)
    }

}