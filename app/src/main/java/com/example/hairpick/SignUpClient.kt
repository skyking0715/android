package com.example.hairpick

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.example.hairpick.databinding.ActivitySignUpClientBinding
import java.lang.Exception

class SignUpClient : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding=ActivitySignUpClientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //갤러리 요청 런처
        val requestGalleryLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            try{

                //비트맵 이미지 크기 설정
                val option=BitmapFactory.Options()
                option.inSampleSize=4


                //이미지 로딩
                var inputStream=contentResolver.openInputStream(it.data!!.data!!)
                val bitmap=BitmapFactory.decodeStream(inputStream,null,option)
                inputStream!!.close()
                inputStream=null
                bitmap?.let{
                    binding.userImageView.setImageBitmap(bitmap)

                }?:{
                    Log.d("Jeon",  "bitmap null")
                }

            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        // 버튼 리스너
        binding.uploadBtn.setOnClickListener{

            //갤러리
            val intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type="image/*"
            requestGalleryLauncher.launch(intent)
        }

    }
}