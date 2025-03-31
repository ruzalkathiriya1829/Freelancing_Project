package com.example.fuelfix.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.fuelfix.R
import com.example.fuelfix.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {

        val backgroundImage: ImageView = findViewById(R.id.imgsplash)
        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.animation)
        backgroundImage.startAnimation(slideAnimation)

        Handler().postDelayed({
            val intent = Intent(this, WelcomeScreenActivity::class.java)
            startActivity(intent)
            finish()
        },5000)

    }
}