package com.example.hairpick

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
import android.widget.RadioButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.hairpick.databinding.ActivityClientInfoUpdateBinding
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.lang.Exception

class ClientInfoUpdate : AppCompatActivity() {
    lateinit var binding :ActivityClientInfoUpdateBinding
    lateinit var firestore: FirebaseFirestore
    lateinit var collectionRef: CollectionReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityClientInfoUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore=FirebaseFirestore.getInstance()
        collectionRef=firestore.collection("clients")

        get_prevInfo()
        //갤러리 요청 런처
        val requestGalleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
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
        //버튼 리스너
        binding.uploadBtn.setOnClickListener{

            //갤러리
            val intent=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type="image/*"
            requestGalleryLauncher.launch(intent)
        }
        binding.updateBtn.setOnClickListener {
            val client=createClient()
            update(client)
        }

    }
    fun ImgSizeSet(fileUri: Uri, reqWidth:Int, reqHeight:Int):Int{
        val options= BitmapFactory.Options()
        options.inJustDecodeBounds=true

        try{
            var inputStream=contentResolver.openInputStream(fileUri)
            BitmapFactory.decodeStream(inputStream,null,options)
            inputStream!!.close()
            inputStream=null
        }catch (e: Exception){ e.printStackTrace()}

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

    fun get_prevInfo(){
        collectionRef.document(MyAccountApplication.email.toString())
            .get()
            .addOnSuccessListener {doc->
               if(doc!=null){
                   val mydata=doc.toObject(ClientInfo::class.java)
                   val prevInfo=ClientInfo()
                   prevInfo.setInfo2(mydata?.imgUrl.toString(), mydata?.name.toString(), mydata?.num.toString(), mydata?.adress.toString())
                   bindData(prevInfo)
               }
            }
            .addOnFailureListener {
                Log.w("Jeon", "Error getting documents")
            }
    }
    fun bindData(prevInfo:ClientInfo){
        Glide.with(binding.userImageView.context)
            .load(prevInfo.imgUrl)
            .into(binding.userImageView)

        binding.idEdit.text=Editable.Factory.getInstance().newEditable(MyAccountApplication.email)
        binding.idEdit.isEnabled=false

        binding.nameEdit.text=Editable.Factory.getInstance().newEditable(prevInfo.name)
        binding.addressEdit.text=Editable.Factory.getInstance().newEditable(prevInfo.adress)
        binding.digitEdit.text=Editable.Factory.getInstance().newEditable(prevInfo.num)

        binding.nameEdit.selectAll()
        binding.addressEdit.selectAll()
        binding.digitEdit.selectAll()

    }
    fun update(client:ClientInfo){
        val storageRef= FirebaseStorage.getInstance().reference
        val imageRef=storageRef.child("clientImages/${MyAccountApplication.email}.jpg")

        val uploadTask=imageRef.putBytes(imgToByte(getBitmapFromView(binding.userImageView)))
        uploadTask.addOnCompleteListener{
                task->
            if(task.isSuccessful) {
                Log.d("jeon", "이미지 스토리지에 업로드 성공")
                // 이미지가 성공적으로 업로드되면 다운로드 URL을 가져옵니다.
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    client.imgUrl = uri.toString()

                    val docRef: DocumentReference =firestore.collection("clients").document(binding.idEdit.text.toString())
                    docRef.set(client)
                        .addOnSuccessListener {
                            nextPageDialog()
                        }
                        .addOnFailureListener {
                                e->Log.w("jeon", "Error adding datas",e)
                            failDialog()
                        }

                }
            }
        }
    }
    fun createClient():ClientInfo{
        val id=binding.idEdit.getText()
        val client=ClientInfo(id.toString())
        lateinit var name:String
        var sex:Int=0
        lateinit var num:String
        lateinit var address:String

        name=binding.nameEdit.getText().toString()
        sex=MyAccountApplication.sex
        num=binding.digitEdit.getText().toString()
        address=binding.addressEdit.getText().toString()

        client.setInfo(name,sex,num,address)
        return client
    }

    //뷰 내용 비트맵 객체로 그리기
    fun getBitmapFromView(view: View): Bitmap {
        var bitmap= Bitmap.createBitmap(view.width,view.height, Bitmap.Config.ARGB_8888)
        var canvas= Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
    //이미지 바이트값으로 변환
    fun imgToByte(bitmap: Bitmap):ByteArray{
        val bitmap=getBitmapFromView(binding.userImageView)
        val baos= ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG,100,baos)
        return baos.toByteArray()
    }

    fun nextPageDialog(){
        val btnHandler=object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
               finish()
            }
        }
        AlertDialog.Builder(this).run{
            setTitle("업데이트 성공!")
            setMessage("프로필 업데이트가 완료되었습니다")
            setPositiveButton("확인",btnHandler)
            setCancelable(false)
            show()
        }.setCanceledOnTouchOutside(false)
    }
    fun failDialog(){
        AlertDialog.Builder(this).run{
            setTitle("업데이트 실패")
            setMessage("다시 시도해주세요.")
            setPositiveButton("확인",null)
            show()
        }
    }
}