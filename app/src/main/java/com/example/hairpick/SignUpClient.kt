package com.example.hairpick

import android.app.ProgressDialog.show
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
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
    var selectedImageUri:Uri?=null
    lateinit var imageUri:Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivitySignUpClientBinding.inflate(layoutInflater)
        auth= Firebase.auth //파이어베이스 인증객체 얻기
        db= FirebaseFirestore.getInstance() //파이어스토어 객체얻기
        setContentView(binding.root)

        //인증했던 메일 정보 자동으로 불러와서, editText 입력 못하게 하기
        binding.idEdit.text= Editable.Factory.getInstance().newEditable(MyAccountApplication.email)
        binding.idEdit.isEnabled=false

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

            if(selectedImageUri!=null){
                //클라이언트 객체 생성
                val client=createClient()
                addData(client)
            }

        }



    }

    fun createClient():ClientInfo{
        val id=binding.idEdit.getText()
        //val pw=binding.pwEdit.getText()
         //signup(id.toString(),pw.toString())

        val client=ClientInfo(id.toString())


        lateinit var name:String
        lateinit var male:RadioButton
        lateinit var female:RadioButton
        var sex:Int=0
        lateinit var num:String
        lateinit var address:String


        name=binding.nameEdit.getText().toString()
        male=binding.radioMale
        female=binding.radioFemale
        sex=if(male.isChecked)1 else 2
        num=binding.digitEdit.getText().toString()
        address=binding.addressEdit.getText().toString()


        client.setInfo(imageUri.toString(),name,sex,num,address)

        return client
    }
    fun addData(client:ClientInfo){
        val docRef:DocumentReference=db.collection("clients").document(binding.idEdit.text.toString())
        docRef.set(client)
            .addOnSuccessListener {
                Log.d("Jeon", "ClientDatas added with ID : ${docRef.id}")
                nextPageDialog()
            }
            .addOnFailureListener {
                    e->Log.w("jeon", "Error adding datas",e)
                failDialog()
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

                selectedImageUri=it.data?.data //이미지 uri
                if(selectedImageUri!=null){
                    imageUri=getRealPathFromURI(selectedImageUri!!).toUri()
                }
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

    //uri->파일경로
    fun getRealPathFromURI(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val filePath = columnIndex?.let { cursor.getString(it) }
        cursor?.close()
        return filePath ?: ""
    }

    fun nextPageDialog(){
        val btnHandler=object:DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                if(p1==DialogInterface.BUTTON_POSITIVE){
                    val intent = Intent(applicationContext, MainFrame::class.java)
                    startActivity(intent)
                }
            }
        }

        AlertDialog.Builder(this).run{
            setTitle("등록 성공!")
            setMessage("프로필 등록이 완료되었습니다")
            setPositiveButton("확인",btnHandler)
            setCancelable(false)
            show()
        }.setCanceledOnTouchOutside(false)
    }
    fun failDialog(){
        AlertDialog.Builder(this).run{
            setTitle("등록 실패")
            setMessage("다시 시도해주세요.")
            setPositiveButton("확인",null)
            show()
        }
    }
}