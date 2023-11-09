package com.example.hairpick

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.hairpick.databinding.Stylist4Binding

class Stylist4 : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sty4 = Stylist4Binding.inflate(layoutInflater)

        setContentView(sty4.root)

    }



}