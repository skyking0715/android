package com.example.hairpick

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hairpick.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}