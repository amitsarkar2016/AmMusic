package com.codetaker.ammusic.ui.onboard

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.codetaker.ammusic.R
import com.codetaker.ammusic.databinding.ActivityOnBoardingBinding
import com.codetaker.ammusic.ui.main.MainActivity
import com.codetaker.ammusic.utils.SharePrefManager


class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding
    private val TAG = "OnBoardingActivity"
    private val PERMISSION_REQUEST_CODE = 100
    private val permissions = arrayOf(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    )
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                navigateToHome()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnGetStarted.setOnClickListener {
            requestPermissionLauncher.launch(permissions)
        }

    }

    private fun navigateToHome() {
        SharePrefManager.getPrefInstance(this).setOnBoardingStatus(true)
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }
}