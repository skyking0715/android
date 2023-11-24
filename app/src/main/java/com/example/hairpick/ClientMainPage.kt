package com.example.hairpick

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hairpick.databinding.FragmentClientMainPageBinding
import com.example.hairpick.databinding.ImgitemBinding
import com.example.hairpick.databinding.RecommendimgitemBinding
import com.example.hairpick.databinding.TrendimgitemBinding
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.storage


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ClientMainPage.newInstance] factory method to
 * create an instance of this fragment.
 */
class ClientMainPage : Fragment() {
    lateinit var binding:FragmentClientMainPageBinding
    lateinit var photoAdapter_trend: TrendImgAdapter
    lateinit var photoAdapter_Rec:RecommendAdapter
    lateinit var db:FirebaseFirestore
    lateinit var storage:FirebaseStorage
    lateinit var storageReference:StorageReference

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        db= FirebaseFirestore.getInstance()
        storage= Firebase.storage



    }

    fun getResourceUri(context: Context,resId:Int):Uri{
       return Uri.parse("android.resource://"+context.packageName+"/"+resId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentClientMainPageBinding.inflate(inflater, container, false)



        photoAdapter_trend = TrendImgAdapter(requireContext())
        photoAdapter_Rec= RecommendAdapter(requireContext())
        val layoutManagerTrend = LinearLayoutManager(requireContext())
        val layoutManagerRec=LinearLayoutManager(requireContext())
        layoutManagerTrend.orientation = LinearLayoutManager.HORIZONTAL
        layoutManagerRec.orientation = LinearLayoutManager.HORIZONTAL


        binding.recyclerView1.layoutManager = layoutManagerTrend
        binding.recyclerView1.adapter = photoAdapter_trend
        binding.recyclerView2.layoutManager=layoutManagerRec
        binding.recyclerView2.adapter=photoAdapter_Rec

        val myInfo=getUserObject{ //유저정보 객체
            clientInfo ->
            if(clientInfo!=null){
                Log.d("jeon", clientInfo.sex.toString())
                MyAccountApplication.sex=clientInfo.sex
                MyAccountApplication.name=clientInfo.name
                setUserName()

                if(MyAccountApplication.sex==1){
                    storageReference=storage.reference.child("manTrend")
                    getTrendImg()
                }else{
                    storageReference=storage.reference.child("womanTrend")
                    getTrendImg()
                }
            }else{
                Log.d("jeon", "데이터 로드 실패")


            }
        }





///////////////////////////////////////////////////////////////////////////////////////
        val recommendImageResources = mapOf(
            "가르마펌" to R.drawable.re1,
            "드롭컷" to R.drawable.re2,
            "리프펌" to R.drawable.re3,
            "쉐도우펌" to R.drawable.re4,
            "슬릭댄디펌" to R.drawable.re5,
            "시스루댄디펌" to R.drawable.re6,
            "시스루애즈펌" to R.drawable.re7,
            "시스루펌" to R.drawable.re8,
            "애즈펌" to R.drawable.re9,
            "히피펌" to R.drawable.re10
        )

        for (resId in recommendImageResources) {
            val hairName:String=resId.key
            val imageUri: Uri = getResourceUri(requireContext(), resId.value)
            photoAdapter_Rec.addPhoto(imageUri,hairName)
        }



        return binding.root
    }

    /*Firestore 쿼리의 비동기적인 특성을 처리하는 콜백
    * 데이터를 성공적으로 가져온 후에도, 비동기적 특성으로 인해 clientInfo가 초기화되지 않는 경우 방지
    * */

    fun getUserObject(callback: (ClientInfo?)->Unit){
        var clientInfo:ClientInfo?=null
        val docRef=db.collection("clients").document(MyAccountApplication.email.toString())
        docRef.get().addOnSuccessListener { document->
            clientInfo=document.toObject(ClientInfo::class.java)
            callback(clientInfo)

        }.addOnFailureListener {
            Log.d("jeon","사용자 데이터 가져오기 실패")
            callback(null)
        }

    }

    fun getTrendImg(){
        storageReference.listAll()
            .addOnSuccessListener { result->
                for(item in result.items){
                    item.downloadUrl.addOnSuccessListener {imageUri->
                        Log.d("jeon","성공성공")
                        photoAdapter_trend.addPhoto(imageUri)
                    }
                        .addOnFailureListener {
                            Log.d("jeon","이미지 다운로드 url 가져오기 실패")
                        }

                }
            }
            .addOnFailureListener {
                Log.d("jeon", "이미지 불러오기 실패")
            }
    }

    fun setUserName(){
        if(MyAccountApplication.name!=null)
            binding.recommendText.text=MyAccountApplication.name+" 님을 위한 추천 스타일"
    }



}

class TrendImgViewHolder(val binding: TrendimgitemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(photoUri: Uri){
        //Glide - 구글에서 만든 이미지 로더 라이브러리
        //기술문서 작성 시 추가할 것
        Glide.with(binding.trendimgData.context)
            .load(photoUri)
            .into(binding.trendimgData)
    }
}

class TrendImgAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        return TrendImgViewHolder(
            TrendimgitemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    //각 항목 구성
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TrendImgViewHolder).binding

        val photoUri = photoList[position]
        holder.bind(photoUri)


    }

    //항목 개수
    override fun getItemCount(): Int {
        return photoList.size
    }
}

class RecommendViewHolder(val binding: RecommendimgitemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(photoUri: Uri,name:String) {
        binding.recommendimgData.setImageURI(photoUri)
        binding.hairName.text=name
    }
}

class RecommendAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val photoList = mutableListOf<Uri>()
    private val hairList= mutableListOf<String>()
    fun addPhoto(uri: Uri,name:String) {
        photoList.add(uri)
        hairList.add(name)
        notifyDataSetChanged()
    }

    //뷰 홀더
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return RecommendViewHolder(
            RecommendimgitemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    //각 항목 구성
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as RecommendViewHolder).binding

        val photoUri = photoList[position]
        val hairName=hairList[position]
        holder.bind(photoUri,hairName)

    }

    //항목 개수
    override fun getItemCount(): Int {
        return photoList.size
    }
}