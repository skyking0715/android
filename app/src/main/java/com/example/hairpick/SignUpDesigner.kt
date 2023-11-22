package com.example.hairpick

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hairpick.databinding.ActivitySignUpDesignerBinding
import com.example.hairpick.databinding.FragmentRequestPageBinding
import com.example.hairpick.databinding.ImgitemBinding
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class SignUpDesigner : AppCompatActivity() {
    lateinit var binding: ActivitySignUpDesignerBinding
    lateinit var photoAdapter: ShopImgAdapter
    var selectedImageUri:Uri?=null
    lateinit var imageUri:Uri
    lateinit var shopImgList:MutableList<Uri>
    private lateinit var getContent: ActivityResultLauncher<Intent>
    var ImgCount:Int=0;
    lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignUpDesignerBinding.inflate(layoutInflater)
        binding.emailEdit.text= Editable.Factory.getInstance().newEditable(MyAccountApplication.email)
        binding.emailEdit.isEnabled=false

        db= FirebaseFirestore.getInstance() //파이어스토어 객체얻기
        shopImgList = mutableListOf()

        photoAdapter = ShopImgAdapter(this)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = photoAdapter

        setContentView(binding.root)

        //갤러리 요청 런처
        val requestGalleryLauncher=requestGallery()



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
            val stylist=createStylist()
            addData(stylist)
        }



    }

    fun createStylist():ShopInfo{
        val id=binding.emailEdit.getText()
        val shop=ShopInfo(id.toString())

        lateinit var name:String
        lateinit var male: RadioButton
        lateinit var female: RadioButton
        var sex:Int=0
        lateinit var num:String

        lateinit var shopName:String
        lateinit var shopDesc:String
        lateinit var shopNum:String
        lateinit var shopAddress:String
        lateinit var priceList:String

        name=binding.nameEdit.getText().toString()
        male=binding.radioMale
        female=binding.radioFemale
        sex=if(male.isChecked)1 else 2
        num=binding.digitEdit.getText().toString()

        shopName=binding.shopNameEdit.getText().toString()
        shopDesc=binding.descEdit.getText().toString()
        shopNum=binding.shopdigitEdit.getText().toString()
        shopAddress=binding.addressEdit.getText().toString()
        priceList=binding.priceinfoEdit.getText().toString()


        shop.stylistInfoset(imageUri.toString(),name,sex,num)
        shop.shopImgSet(shopImgList)
        shop.shopInfoset(shopName,shopDesc,shopNum,shopAddress,priceList)

        return shop
    }
    fun addData(stylist:ShopInfo){
        val docRef: DocumentReference =db.collection("stylists").document(binding.emailEdit.text.toString())
        docRef.set(stylist)
            .addOnSuccessListener {
                Log.d("Jeon", "StylistDatas added with ID : ${docRef.id}")
                nextPageDialog()
            }
            .addOnFailureListener {
                    e->Log.w("jeon", "Error adding datas",e)
                failDialog()
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
                    binding.profileView.setImageBitmap(bitmap)

                } ?: {
                    Log.d("Jeon", "bitmap null")
                }

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        return requestGalleryLauncher
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

    //선택된 이미지에 대한 처리
    private fun handleGalleryChoice(data: Intent?) {
        if(ImgCount==4)
            return
        data?.data?.let { selectedImageUri ->
            photoAdapter.addPhoto(selectedImageUri)
            //ShopInfo 객체 초기화용
            try{
                shopImgList.add(selectedImageUri)
            }catch (e:Exception){
                Log.e("Jeon", "Error adding image",e)
            }
            binding.galleryBtn.text=photoAdapter.itemCount.toString() + " / 4"
            ImgCount++
        }
    }

    fun nextPageDialog(){
        val btnHandler=object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                if(p1== DialogInterface.BUTTON_POSITIVE){
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