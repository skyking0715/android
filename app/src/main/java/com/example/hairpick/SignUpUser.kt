package com.example.hairpick

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.hairpick.databinding.ActivitySignUpUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class SignUpUser : AppCompatActivity() {
    lateinit var binding:ActivitySignUpUserBinding
    lateinit var auth: FirebaseAuth
    //val intent = Intent(this, Mainpage1::class.java)
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
                    nextPageDialog()

                    /*val intent = Intent(this, SignUpClient::class.java)
                    startActivity(intent)*/
                }else{
                    Log.d("Jeon", "인증메일 전송 실패")
                    failDialog()
                }
                }
            }
            else{
                Log.w("jeon", "파이어베이스 등록 실패",task.exception)
            }

        }
    }
    fun nextPageDialog(){
        val btnHandler=object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                if(p1== DialogInterface.BUTTON_POSITIVE){
                    //startActivity(intent)
                }
            }
        }

        AlertDialog.Builder(this).run{
            setTitle("인증메일이 전송되었습니다!")
            setMessage("인증 완료 후 로그인해주세요")
            setPositiveButton("확인",btnHandler)
            setCancelable(false)
            show()
        }.setCanceledOnTouchOutside(false)
    }
    fun failDialog(){
        AlertDialog.Builder(this).run{
            setTitle("인증메일 전송 실패")
            setMessage("이메일/비밀번호를 다시 확인해주세요.")
            setPositiveButton("확인",null)
            show()
        }
    }
}