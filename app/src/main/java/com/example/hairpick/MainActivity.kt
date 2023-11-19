package com.example.hairpick

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hairpick.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)



        binding.userSignUp.setOnClickListener {
            val intent = Intent(this, SignUpUser::class.java)
            startActivity(intent)
        }
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
        binding.clientSignUp.setOnClickListener{
            val intent = Intent(this, SignUpClient::class.java)
            startActivity(intent)
        }
        binding.mainFrameBtn.setOnClickListener{
            val intent = Intent(this, MainFrame::class.java)
            startActivity(intent)
        }
        binding.clientBidBtn.setOnClickListener {
            val intent = Intent(this, ClientBid::class.java)
            startActivity(intent)
        }

        binding.stylistSignUp.setOnClickListener{
            val intent = Intent(this, SignUpDesigner::class.java)
            startActivity(intent)
        }

        binding.stylistUpdate.setOnClickListener {
            val intent = Intent(this, DesignerInfoUpdate::class.java)
            startActivity(intent)
        }

        binding.clientUpdate.setOnClickListener {
            val intent = Intent(this, ClientInfoUpdate::class.java)
            startActivity(intent)
        }

        setContentView(binding.root)
    }




}