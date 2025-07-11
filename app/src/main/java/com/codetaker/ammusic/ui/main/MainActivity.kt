package com.codetaker.ammusic.ui.main


import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.codetaker.ammusic.R
import com.codetaker.ammusic.databinding.ActivityMainBinding
import com.codetaker.ammusic.extensions.replaceFragment
import com.codetaker.ammusic.extensions.setStatusBarColor
import com.codetaker.ammusic.ui.home.HomeFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainContainer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        setStatusBarColor(window, ContextCompat.getColor(this, R.color.statusColor))

        val fragment = HomeFragment()
        replaceFragment(fragment, R.id.mainContainer, false)
    }

}