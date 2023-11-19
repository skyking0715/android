package com.example.hairpick

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.hairpick.databinding.ActivitySignUpClientBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream
import java.lang.Exception

class SignUpClient : AppCompatActivity() {
    lateinit var binding:ActivitySignUpClientBinding
    lateinit var auth:FirebaseAuth
    lateinit var db:FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivitySignUpClientBinding.inflate(layoutInflater)
        auth= Firebase.auth //파이어베이스 인증객체 얻기
        db= FirebaseFirestore.getInstance() //파이어스토어 객체얻기
        setContentView(binding.root)

        //갤러리 요청 런처
        val requestGalleryLauncher= requestGallery()

        // 버튼 리스너
        binding.uploadBtn.setOnClickListener{

            //갤러리
            val intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type="image/*"
            requestGalleryLauncher.launch(intent)
        }
        binding.signUpBtn.setOnClickListener{
            //클라이언트 객체 생성
            val client=createClient()
            addData(client)
        }



    }

    fun createClient():ClientInfo{
        val id=binding.idEdit.getText()
        val pw=binding.pwEdit.getText()
        Log.d("Jeon", "$id + $pw")
         signup(id.toString(),pw.toString())

        val client=ClientInfo(id.toString(),pw.toString())

        lateinit var img:ByteArray
        lateinit var name:String
        lateinit var male:RadioButton
        lateinit var female:RadioButton
        var sex:Int=0
        lateinit var num:String
        lateinit var address:String

        img=imgToByte(getBitmapFromView(binding.userImageView))
        name=binding.nameEdit.getText().toString()
        male=binding.radioMale
        female=binding.radioFemale
        sex=if(male.isChecked)1 else 2
        num=binding.digitEdit.getText().toString()
        address=binding.addressEdit.getText().toString()
        client.setInfo(img,name,sex,num,address)

        return client
    }

    fun signup(email:String, password:String){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this){task->
            if(task.isSuccessful){
                Log.d("Jeon", "파이어베이스 등록 성공")

                auth.currentUser?.sendEmailVerification()?.addOnCompleteListener{
                    sendTask->if(sendTask.isSuccessful){
                    Log.d("Jeon", "인증메일 전송 성공")
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

    fun addData(client:ClientInfo){
        val colRef:CollectionReference=db.collection("clients")
        val docRef: Task<DocumentReference> = colRef.add(client)

        docRef.addOnSuccessListener { documentReferece->
            Log.d("Jeon", "ClientDatas added with ID : ${documentReferece.id}")
        }
        docRef.addOnFailureListener{
            e->Log.w("jeon", "Error adding datas",e)
        }
    }

    //뷰 내용 비트맵 객체로 그리기
    fun getBitmapFromView(view: View):Bitmap{
        var bitmap=Bitmap.createBitmap(view.width,view.height,Bitmap.Config.ARGB_8888)
        var canvas=Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
    //이미지 바이트값으로 변환
    fun imgToByte(bitmap:Bitmap):ByteArray{
        val bitmap=getBitmapFromView(binding.userImageView)
        val baos=ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG,100,baos)
        return baos.toByteArray()
    }

    fun requestGallery():ActivityResultLauncher<Intent> {
        val requestGalleryLauncher= registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            try {

                //비트맵 이미지 크기 설정
                val ratio = ImgSizeSet(
                    it.data!!.data!!,
                    resources.getDimensionPixelSize(R.dimen.imgSize),
                    resources.getDimensionPixelSize(R.dimen.imgSize)
                )
                val option = BitmapFactory.Options()
                option.inSampleSize = ratio


                //이미지 로딩
                var inputStream = contentResolver.openInputStream(it.data!!.data!!)
                val bitmap = BitmapFactory.decodeStream(inputStream, null, option)
                inputStream!!.close()
                inputStream = null
                bitmap?.let {
                    binding.userImageView.setImageBitmap(bitmap)

                } ?: {
                    Log.d("Jeon", "bitmap null")
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return requestGalleryLauncher
    }

    fun ImgSizeSet(fileUri: Uri, reqWidth:Int, reqHeight:Int):Int{
        val options=BitmapFactory.Options()
        options.inJustDecodeBounds=true

        try{
            var inputStream=contentResolver.openInputStream(fileUri)
            BitmapFactory.decodeStream(inputStream,null,options)
            inputStream!!.close()
            inputStream=null
        }catch (e:Exception){ e.printStackTrace()}

        val (height:Int, width:Int)=options.run{
            outHeight to outWidth
        }
        var inSampleSize=1

        if(height>reqHeight || width>reqWidth){
            val halfHeight:Int=height/2
            val halfWidth:Int=width/2

            while(halfHeight/inSampleSize >= reqHeight && halfWidth/inSampleSize >=reqWidth){
                inSampleSize*=2
            }
        }
        return inSampleSize
    }
}