package com.example.hairpick

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.hairpick.databinding.ActivitySignInPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInPage : AppCompatActivity() {
    lateinit var auth:FirebaseAuth
    lateinit var email:String
    lateinit var password:String
    //val intentClient = Intent(this, SignUpClient::class.java)
    //val intentStylist = Intent(this, SignUpDesigner::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding=ActivitySignInPageBinding.inflate(layoutInflater)
        auth=Firebase.auth
        setContentView(binding.root)



        //버튼 리스너
        binding.signInCli.setOnClickListener {
            email=binding.idEdit.getText().toString()
            password=binding.pwEdit.getText().toString()

            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){
                task->
                if(task.isSuccessful){
                    //로그인 성공
                    nextPageDialog_Cli()
                }else{
                    //로그인 실패
                    failDialog()
                }
            }
        }
        binding.signInSty.setOnClickListener {
            email=binding.idEdit.getText().toString()
            password=binding.pwEdit.getText().toString()

            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){
                task->
            if(task.isSuccessful){
                //로그인 성공
               nextPageDialog_Sty()

            }else{
                //로그인 실패
              failDialog()
            }
        }

        }

    }
    fun nextPageDialog_Cli(){
        val btnHandler=object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                if(p1== DialogInterface.BUTTON_POSITIVE){
                    //startActivity(intentClient)
                }
            }
        }

        AlertDialog.Builder(this).run{
            setTitle("로그인 성공!")
            setMessage("환영합니다")
            setPositiveButton("확인",btnHandler)
            setCancelable(false)
            show()
        }.setCanceledOnTouchOutside(false)
    }
    fun nextPageDialog_Sty(){
        val btnHandler=object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                if(p1== DialogInterface.BUTTON_POSITIVE){
                    //startActivity(intentStylist)
                }
            }
        }

        AlertDialog.Builder(this).run{
            setTitle("로그인 성공!")
            setMessage("환영합니다")
            setPositiveButton("확인",btnHandler)
            setCancelable(false)
            show()
        }.setCanceledOnTouchOutside(false)
    }
    fun failDialog(){
        AlertDialog.Builder(this).run{
            setTitle("로그인 실패")
            setMessage("아이디/비밀번호를 다시 확인해주세요")
            setPositiveButton("확인",null)
            show()
        }
    }
}