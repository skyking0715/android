package com.example.hairpick

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.hairpick.databinding.ActivitySignUpUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpUser : AppCompatActivity() {
    lateinit var binding:ActivitySignUpUserBinding
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignUpUserBinding.inflate(layoutInflater)
        auth= Firebase.auth //파이어베이스 인증객체 얻기
        setContentView(binding.root)

        val email=binding.idEdit.getText()
        val password=binding.pwEdit.getText()
        Log.d("Jeon","$email + $password ")

        binding.signUpBtn.setOnClickListener {
            Log.d("Jeon","$email + $password ")
            signup(email.toString(),password.toString())
        }


    }

    fun signup(email:String, password:String){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this){task->
            if(task.isSuccessful){
                Log.d("Jeon", "파이어베이스 등록 성공")

                auth.currentUser?.sendEmailVerification()?.addOnCompleteListener{
                        sendTask->if(sendTask.isSuccessful){
                    Log.d("Jeon", "인증메일 전송 성공")

                    val intent = Intent(this, SignUpClient::class.java)
                    startActivity(intent)
                }else{
                    Log.d("Jeon", "인증메일 전송 실패")
                }
                }
            }
            else{
                Log.w("jeon", "파이어베이스 등록 실패",task.exception)
            }

        }
    }
}