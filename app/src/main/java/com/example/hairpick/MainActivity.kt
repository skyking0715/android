package com.example.hairpick

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.hairpick.databinding.ActivityMainBinding
import com.example.hairpick.databinding.MainOneBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)




        binding.main1btn.setOnClickListener {
            val intent = Intent(this, Mainpage1::class.java)
            startActivity(intent)
        }

        binding.client3btn.setOnClickListener {
            val intent = Intent(this, Client3::class.java)
            startActivity(intent)
        }

        binding.client4btn.setOnClickListener {
            val intent = Intent(this, Client4::class.java)
            startActivity(intent)
        }
        binding.stylist4btn.setOnClickListener {
            val intent = Intent(this, Stylist4::class.java)
            startActivity(intent)
        }

        setContentView(binding.root)
    }




}