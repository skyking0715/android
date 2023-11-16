package com.example.hairpick

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hairpick.databinding.ActivityClientInfoUpdateBinding

class ClientInfoUpdate : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding=ActivityClientInfoUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.updateBtn.setOnClickListener{

        }
    }
}