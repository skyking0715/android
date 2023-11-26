package com.example.hairpick

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hairpick.databinding.ActivitySignInPageBinding
import com.example.hairpick.databinding.Stylist4Binding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Stylist4 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding= Stylist4Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}