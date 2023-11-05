package com.example.hairpick

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.hairpick.databinding.ActivityMainBinding
import com.example.hairpick.databinding.MainOneBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)


        val main1 = MainOneBinding.inflate(layoutInflater)
        main1.accountNone.setOnClickListener{

            Toast.makeText(applicationContext, "text Clicked", Toast.LENGTH_LONG).show()



        }


        setContentView(main1.root)
    }
}