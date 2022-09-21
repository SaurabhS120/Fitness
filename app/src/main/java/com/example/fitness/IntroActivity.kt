package com.example.fitness

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fitness.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        val binding = ActivityIntroBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)
        binding.nextButton.setOnClickListener {
            finish()
        }

    }
}