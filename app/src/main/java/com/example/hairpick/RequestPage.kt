package com.example.hairpick

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.icu.text.ListFormatter.Width
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hairpick.databinding.FragmentRequestPageBinding
import com.example.hairpick.databinding.FragmentStylist4Binding
import com.example.hairpick.databinding.ImgitemBinding
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.NonDisposableHandle.parent
import java.io.IOException
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RequestPage.newInstance] factory method to
 * create an instance of this fragment.
 */
class RequestPage : Fragment() {
    lateinit var binding: FragmentRequestPageBinding
    lateinit var photoAdapter: ReImgAdapter
    private lateinit var getContent: ActivityResultLauncher<Intent>
    var ImgCount: Int = 0;
    lateinit var refImgList:MutableList<Uri>
    lateinit var db: FirebaseFirestore

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        db= FirebaseFirestore.getInstance() //파이어스토어 객체얻기
        refImgList = mutableListOf()
        getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    handleGalleryChoice(data)

                }
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRequestPageBinding.inflate(inflater, container, false)


        photoAdapter = ReImgAdapter(requireContext())
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL


        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = photoAdapter

        binding.galleryBtn.setOnClickListener {
            openGallery()
        }
        binding.requestBtn.setOnClickListener {
            createRequestAndUploadImages()
        }

        return binding.root
    }

    fun createRequest(): requestInfo {
        val id = MyAccountApplication.email.toString()
        val address = MyAccountApplication.address.toString()
        val profile = MyAccountApplication.profile.toString()
        val request = requestInfo(id, address)

        lateinit var title: String
        lateinit var desc: String


        title = binding.titleEdit.getText().toString()
        desc = binding.descEdit.getText().toString()


        request.reqInfoset(profile, title, desc)
        return request
    }

    // 이미지 업로드 및 URL 획득이 완료된 후에 shopInfo의 img에 저장(동기화하기 위해서)
    private fun createRequestAndUploadImages() {
        val request = createRequest()
        // 헤어샵 이미지 업로드 및 URL 획득
        uploadShopImagesAndSetUrls(request)
    }
    private fun uploadShopImagesAndSetUrls(request: requestInfo) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageUrls = mutableListOf<String>()

        val uploadTasks = mutableListOf<UploadTask>()

        for (i in 0 until refImgList.size) {
            val imageUri = refImgList[i]
            val imageName = "req_image_$i.jpg"
            val imageRef = storageRef.child("reqImages/${MyAccountApplication.email}/$imageName")

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
                    if (imageUrls.size == refImgList.size) {
                        request.refImgSet(imageUrls)
                    }
                }
            }
        }

        //모든 이미지 업로드 작업 대기
        Tasks.whenAllComplete(uploadTasks)
            .addOnCompleteListener { allCompleteTask ->
                if (allCompleteTask.isSuccessful) {
                    // 모든 이미지가 성공적으로 업로드되면 Firestore에 데이터 추가
                    addData(request)
                } else {
                    // 하나 이상의 이미지 업로드가 실패하면 실패 다이얼로그 표시
                    failDialog()
                    Log.e("Jeon", "Error uploading images")
                    allCompleteTask.exception?.printStackTrace()
                }
            }
    }
    fun addData(request: requestInfo){
        val docRef: DocumentReference =db.collection("requests").document(MyAccountApplication.email.toString())
        docRef.set(request)
            .addOnSuccessListener {
                Log.d("Jeon", "RequestDatas added with ID : ${docRef.id}")

                nextPageDialog()
            }
            .addOnFailureListener {
                    e->Log.w("jeon", "Error adding datas",e)
                failDialog()
            }
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        // 갤러리에서 선택한 이미지 처리

        getContent.launch(intent)
    }

    //선택된 이미지에 대한 처리
    private fun handleGalleryChoice(data: Intent?) {
        if(ImgCount==5)
            return
        data?.data?.let { selectedImageUri ->
            photoAdapter.addPhoto(selectedImageUri)
            //reqInfo 객체 초기화용
            try{
                refImgList.add(selectedImageUri)
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

                }
            }
        }

        AlertDialog.Builder(requireContext()).run{
            setTitle("등록 성공!")
            setMessage("요청서가 성공적으로 등록되었습니다!")
            setPositiveButton("확인",btnHandler)
            setCancelable(false)
            show()
        }.setCanceledOnTouchOutside(false)
    }
    fun failDialog(){
        AlertDialog.Builder(requireContext()).run{
            setTitle("등록 실패")
            setMessage("다시 시도해주세요.")
            setPositiveButton("확인",null)
            show()
        }
    }


}
class ReImgViewHolder(val binding: ImgitemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(photoUri: Uri) {
        binding.imgData.setImageURI(photoUri)
    }
}

class ReImgAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        return ReImgViewHolder(
            ImgitemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    //각 항목 구성
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ReImgViewHolder).binding

        val photoUri = photoList[position]
        holder.bind(photoUri)

    }

    //항목 개수
    override fun getItemCount(): Int {
        return photoList.size
    }
}







