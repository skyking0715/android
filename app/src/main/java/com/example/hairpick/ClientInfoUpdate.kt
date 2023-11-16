package com.example.hairpick

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.example.hairpick.databinding.ActivityClientInfoUpdateBinding
import java.lang.Exception

class ClientInfoUpdate : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityClientInfoUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
}