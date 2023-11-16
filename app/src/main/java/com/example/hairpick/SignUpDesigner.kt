package com.example.hairpick

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hairpick.databinding.ActivitySignUpDesignerBinding
import com.example.hairpick.databinding.FragmentRequestPageBinding
import com.example.hairpick.databinding.ImgitemBinding

class SignUpDesigner : AppCompatActivity() {
    lateinit var binding: ActivitySignUpDesignerBinding
    lateinit var photoAdapter: ShopImgAdapter
    private lateinit var getContent: ActivityResultLauncher<Intent>
    var ImgCount:Int=0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignUpDesignerBinding.inflate(layoutInflater)

        photoAdapter = ShopImgAdapter(this)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = photoAdapter

        setContentView(binding.root)

        //갤러리 요청 런처
        val requestGalleryLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            try{
                //비트맵 이미지 크기 설정
                val ratio=ImgSizeSet(it.data!!.data!!, resources.getDimensionPixelSize(R.dimen.imgSize), resources.getDimensionPixelSize(R.dimen.imgSize))
                val option= BitmapFactory.Options()
                option.inSampleSize=ratio


                //이미지 로딩
                var inputStream=contentResolver.openInputStream(it.data!!.data!!)
                val bitmap= BitmapFactory.decodeStream(inputStream,null,option)
                inputStream!!.close()
                inputStream=null
                bitmap?.let{
                    binding.profileView.setImageBitmap(bitmap)

                }?:{
                    Log.d("Jeon",  "bitmap null")
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }


        getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    handleGalleryChoice(data)

                }
            }



        //버튼 리스너
        binding.profileBtn.setOnClickListener{

            //갤러리
            val intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type="image/*"
            requestGalleryLauncher.launch(intent)
        }
        binding.galleryBtn.setOnClickListener{
            openGallery()

        }
        binding.signUpBtn.setOnClickListener{

        }



    }

    fun ImgSizeSet(fileUri: Uri, reqWidth:Int, reqHeight:Int):Int{
        val options=BitmapFactory.Options()
        options.inJustDecodeBounds=true

        try{
            var inputStream=contentResolver.openInputStream(fileUri)
            BitmapFactory.decodeStream(inputStream,null,options)
            inputStream!!.close()
            inputStream=null
        }catch (e: java.lang.Exception){ e.printStackTrace()}

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

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        // 갤러리에서 선택한 이미지 처리
        getContent.launch(intent)

    }

    //선택된 이미지에 대한 처리
    private fun handleGalleryChoice(data: Intent?) {
        if(ImgCount==4)
            return
        data?.data?.let { selectedImageUri ->
            photoAdapter.addPhoto(selectedImageUri)
            binding.galleryBtn.text=photoAdapter.itemCount.toString() + " / 4"
            ImgCount++
        }
    }


}

class ShopImgViewHolder(val binding: ImgitemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(photoUri: Uri) {
        binding.imgData.setImageURI(photoUri)
    }
}

class ShopImgAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val photoList = mutableListOf<Uri>()
    fun addPhoto(uri: Uri) {
        photoList.add(uri)
        notifyDataSetChanged()
    }

    //뷰 홀더
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return ShopImgViewHolder(
            ImgitemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    //각 항목 구성
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       (holder as ShopImgViewHolder).binding

        val photoUri = photoList[position]
        holder.bind(photoUri)

    }

    //항목 개수
    override fun getItemCount(): Int {
        return photoList.size
    }
}