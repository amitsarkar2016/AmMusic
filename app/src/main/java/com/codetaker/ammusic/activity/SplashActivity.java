package com.codetaker.ammusic.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.codetaker.ammusic.R;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        TextView tv = findViewById(R.id.tv_app_name);
        Shader textShader = new LinearGradient(0, 0, tv.getPaint().measureText(tv.getText().toString()), tv.getTextSize(),
                new int[]{Color.parseColor("#FE8A80"), Color.parseColor("#FE80AB"), Color.parseColor("#8B9DFE"), Color.parseColor("#80D7FE"), Color.parseColor("#01E4FE")},
                null, Shader.TileMode.CLAMP);
        tv.getPaint().setShader(textShader);
        new Handler().postDelayed(() -> startActivity(new Intent(getApplicationContext(), NewMainActivity.class)), 0);
    }
}