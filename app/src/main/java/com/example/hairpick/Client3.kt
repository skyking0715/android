package com.example.hairpick

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.hairpick.databinding.Client3Binding

class Client3 : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cli3 = Client3Binding.inflate(layoutInflater)

        setContentView(cli3.root)

    }


}