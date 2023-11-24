package com.example.hairpick

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.hairpick.databinding.ActivitySignInPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class SignInPage : AppCompatActivity() {
    lateinit var auth:FirebaseAuth
    lateinit var email:String
    lateinit var password:String
    lateinit  var db:FirebaseFirestore
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
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null && MyAccountApplication.checkAuth()) {
                        // 사용자는 이메일 인증을 완료한 상태입니다.
                        MyAccountApplication.email=email
                        //로그인 성공
                        nextPageDialog_Cli()

                    } else {
                        // 사용자는 이메일 인증을 완료하지 않은 상태입니다.
                        authFail()
                    }

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
                if(MyAccountApplication.checkAuth()){
                    //로그인 성공
                    MyAccountApplication.email=email
                    nextPageDialog_Sty()
                }else{
                    Toast.makeText(baseContext,"전송된 메일로 이메일 인증을 진행해주세요.",Toast.LENGTH_SHORT).show()
                }
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

                    db= FirebaseFirestore.getInstance()
                    val document=db.collection("clients").document(email)
                    document.get().addOnSuccessListener {
                        doc->
                        if(doc.data!=null){
                            Log.d("Jeon", "${doc.data}")
                            //유저 정보 존재시 mainFrame 페이지로 이동
                            val intentClient = Intent(applicationContext, MainFrame::class.java)
                            startActivity(intentClient)
                        }else{
                            //유저 정보 없으면 signUpClient 페이지로 이동
                            val intentClient = Intent(applicationContext, SignUpClient::class.java)
                            startActivity(intentClient)
                        }
                    }
                        .addOnFailureListener{
                            exception->
                            Log.d("Jeon", "get failed with ", exception)
                        }

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


                        db= FirebaseFirestore.getInstance()
                        val document=db.collection("stylists").document(email)
                        document.get().addOnSuccessListener {
                                doc->
                            if(doc.data!=null){
                                //유저 정보 존재시 mainFrame 페이지로 이동
                                //TODO:스타일리스트용 mainFrame 만들기
                                val intentClient = Intent(applicationContext, MainFrame::class.java)
                                startActivity(intentClient)
                            }else{
                                //유저 정보 없으면 signUpClient 페이지로 이동
                                val intentClient = Intent(applicationContext, SignUpDesigner::class.java)
                                startActivity(intentClient)
                            }
                        }
                            .addOnFailureListener{
                                    exception->
                                Log.d("Jeon", "get failed with ", exception)
                            }

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
    fun authFail(){
        AlertDialog.Builder(this).run{
            setTitle("로그인 실패")
            setMessage("이메일 인증을 진행해주세요.")
            setPositiveButton("확인",null)
            show()
        }
    }

}