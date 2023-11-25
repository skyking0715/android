package com.example.hairpick

import android.app.Activity
import android.content.Context
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
import android.view.LayoutInflater
import android.view.View
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
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.util.UUID

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
           /* val stylist=createStylist()
            addData(stylist)*/
            createStylistAndUploadImages()
        }



    }
    //프로필 사진 스토리지 등록하고, 그 url을 가져옴
    private fun uploadStylistImageAndGetUrl(shopinfo:ShopInfo,callback: (url: String) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("stylistImages/${binding.emailEdit.getText()}.jpg")

        val uploadTask = imageRef.putBytes(imgToByte(getBitmapFromView(binding.profileView)))

        uploadTask.addOnCompleteListener{
                task->
            if(task.isSuccessful) {
                Log.d("jeon", "이미지 스토리지에 업로드 성공")
                // 이미지가 성공적으로 업로드되면 다운로드 URL을 가져옵니다.
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri.toString())
                }
            }
        }
    }
    // 이미지 업로드 및 URL 획득이 완료된 후에 shopInfo의 img에 저장(동기화하기 위해서)
    private fun createStylistAndUploadImages() {
        val stylist = createStylist()

        // 프로필 이미지 업로드 및 URL 획득
        uploadStylistImageAndGetUrl(stylist){ profileImageUrl ->
            stylist.img = profileImageUrl

            // 헤어샵 이미지 업로드 및 URL 획득
            uploadShopImagesAndSetUrls(stylist)
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


        shop.stylistInfoset(name,sex,num) //프로필 정보 등록
        shop.shopInfoset(shopName,shopDesc,shopNum,shopAddress,priceList) //헤어샾 정보 등록

        return shop
    }

    private fun uploadShopImagesAndSetUrls(shop: ShopInfo) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageUrls = mutableListOf<String>()

        val uploadTasks = mutableListOf<UploadTask>()

        for (i in 0 until shopImgList.size) {
            val imageUri = shopImgList[i]
            val imageName = "shop_image_$i.jpg"
            val imageRef = storageRef.child("shopImages/${binding.emailEdit.getText()}/$imageName")

            val uploadTask = imageRef.putFile(imageUri)

            uploadTasks.add(uploadTask)

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener {
                    task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    imageUrls.add(downloadUri.toString())

                    // 모든 이미지가 업로드되면 ShopInfo에 저장
                    if (imageUrls.size == shopImgList.size) {
                        shop.shopImgSet(imageUrls)
                    }
                }
            }
        }

        //모든 이미지 업로드 작업 대기
        Tasks.whenAllComplete(uploadTasks)
            .addOnCompleteListener { allCompleteTask ->
                if (allCompleteTask.isSuccessful) {
                    // 모든 이미지가 성공적으로 업로드되면 Firestore에 데이터 추가
                    addData(shop)
                } else {
                    // 하나 이상의 이미지 업로드가 실패하면 실패 다이얼로그 표시
                    failDialog()
                    Log.e("Jeon", "Error uploading images")
                    allCompleteTask.exception?.printStackTrace()
                }
            }
    }

    fun addData(stylist:ShopInfo){
        val docRef: DocumentReference =db.collection("stylists").document(binding.emailEdit.text.toString())
        docRef.set(stylist)
            .addOnSuccessListener {
                Log.d("Jeon", "StylistDatas added with ID : ${docRef.id}")

                //shopImgSet(stylist.shopImg)
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
                    //imageUri=getRealPathFromURI(selectedImageUri!!).toUri()
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
    fun getBitmapFromView(view: View): Bitmap {
        var bitmap= Bitmap.createBitmap(view.width,view.height, Bitmap.Config.ARGB_8888)
        var canvas= Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
    //이미지 바이트값으로 변환
    fun imgToByte(bitmap:Bitmap):ByteArray{
        val bitmap=getBitmapFromView(binding.profileView)
        val baos= ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG,100,baos)
        return baos.toByteArray()
    }

    //선택된 이미지에 대한 처리
    private fun handleGalleryChoice(data: Intent?) {
        if(ImgCount==5)
            return
        data?.data?.let { selectedImageUri ->
            photoAdapter.addPhoto(selectedImageUri)
            //ShopInfo 객체 초기화용
            try{
                shopImgList.add(selectedImageUri)
            }catch (e:Exception){
                Log.e("Jeon", "Error adding image",e)
            }
            binding.galleryBtn.text=photoAdapter.itemCount.toString() + " / 5"
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