package com.example.hairpick

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.hairpick.databinding.Chatting1Binding


class Chatting : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = Chatting1Binding.inflate(layoutInflater)

        setContentView(binding.root)

    }



}