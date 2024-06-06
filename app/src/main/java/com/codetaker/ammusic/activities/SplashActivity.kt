package com.codetaker.ammusic.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.codetaker.ammusic.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash)
        val tv = findViewById<TextView>(R.id.tv_app_name)
        val textShader: Shader = LinearGradient(
            0f, 0f, tv.paint.measureText(tv.getText().toString()), tv.textSize, intArrayOf(
                Color.parseColor("#FE8A80"),
                Color.parseColor("#FE80AB"),
                Color.parseColor("#8B9DFE"),
                Color.parseColor("#80D7FE"),
                Color.parseColor("#01E4FE")
            ), null, Shader.TileMode.CLAMP
        )
        tv.paint.setShader(textShader)

        lifecycleScope.launch {
            delay(1500)
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finishAffinity()
        }
    }
}