package com.example.hairpick

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hairpick.MyAccountApplication.Companion.name
import com.example.hairpick.databinding.FragmentStylist4Binding
import com.example.hairpick.databinding.FragmentStylistShopBinding
import com.example.hairpick.databinding.ImgitemBinding
import com.example.hairpick.databinding.ItemClientBidBinding
import com.example.hairpick.databinding.Stylist4RequestitemBinding
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.util.concurrent.CountDownLatch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Stylist_4.newInstance] factory method to
 * create an instance of this fragment.
 */
class Request(){
    lateinit var id:String
    lateinit var profile: String
    lateinit var title:String
    lateinit var desc:String
    //Shop 클래스는 대표 이미지와 가게이름
    constructor(id:String,profile: String, title: String, desc:String):this(){
        this.id=id
        this.profile=profile
        this.title=title
        this.desc=desc
    }
}
class Stylist_4 : Fragment() {
    lateinit var binding:FragmentStylist4Binding
    lateinit var itembinding:Stylist4RequestitemBinding
    lateinit var db: FirebaseFirestore
    //lateinit var storage: FirebaseStorage
    lateinit var firestore: FirebaseFirestore
    lateinit var collectionRef: CollectionReference
    lateinit var datas:MutableList<Request>
    lateinit var adapter:S4Adapter
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

        firestore=FirebaseFirestore.getInstance()
        collectionRef=firestore.collection("requests")
        datas = mutableListOf<Request>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentStylist4Binding.inflate(inflater,container,false)
        itembinding= Stylist4RequestitemBinding.inflate(layoutInflater)

        getReqDatas()
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.stylist4Recycle.layoutManager=layoutManager

        adapter = S4Adapter(datas)
        binding.stylist4Recycle.adapter = adapter

        return binding.root
    }
    fun getReqDatas(){

        val searchAddress:String=MyAccountApplication.address.toString()
        Log.d("Jeon", searchAddress)
        collectionRef.whereEqualTo("address",searchAddress)
            .get()
            .addOnSuccessListener {
                for(document in it){
                    val reqDoc=document.toObject(requestInfo::class.java)
                    val request=Request(reqDoc.id,reqDoc.profile,reqDoc.title,reqDoc.desc) //이미지리스트 제외 리퀘스트 정보 가져오기

                    //TODO: 레퍼런스 이미지 리스트 가져오기
                    /*if (clientId!=null)
                        storageRef=storage.reference.child("reqImages/"+clientId)*/
                    //adapter.addRequest(request)
                    //dataBinding(itembinding,request)
                    datas.add(request)
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.w("Jeon", "Error getting documents")
            }

    }

}

class S4ViewHolder(val binding: Stylist4RequestitemBinding):
    RecyclerView.ViewHolder(binding.root){

    }

class S4Adapter(val datas: MutableList<Request>):
//@TODO 1: datas 원소타입 바꾸기
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val storage= Firebase.storage
   /* fun addRequest(request: Request) {
        datas.add(request)
        notifyDataSetChanged()
    }*/
    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return S4ViewHolder(Stylist4RequestitemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as S4ViewHolder).binding
        val request = datas[position]

        // 프로필 이미지, 제목, 설명 설정
        Glide.with(binding.root.context).load(request.profile).into(binding.profileView)
        binding.titleView.text = request.title
        binding.descView.text = request.desc

        // 참고 이미지 리스트 가져오기 및 설정
        val storageRef = storage.reference.child("reqImages/" +datas[position].id)
        storageRef.listAll()
            .addOnSuccessListener { result ->
                val urlList = mutableListOf<Uri>()

                val tasks = result.items.map { item ->
                    val downloadTask=item.downloadUrl.addOnSuccessListener { imageUri ->
                        urlList.add(imageUri)
                    }
                        .addOnFailureListener {
                            Log.d("jeon","이미지 다운로드 실패")
                        }
                    downloadTask
                }

                Tasks.whenAllComplete(tasks)
                    .addOnSuccessListener {
                        for ((index, uri) in urlList.withIndex()) {
                            // 동적으로 이미지 뷰 설정
                            when (index) {
                                0 -> Glide.with(binding.root.context).load(uri).into(binding.image0)
                                1 -> Glide.with(binding.root.context).load(uri).into(binding.image1)
                                2 -> Glide.with(binding.root.context).load(uri).into(binding.image2)
                                else -> {
                                    // 이미지 뷰의 개수를 초과하는 경우 처리
                                    Log.e("jeon", "Image view count exceeded the limit.")
                                }
                            }
                        }
                    }
                    .addOnFailureListener {
                        Log.d("jeon", "이미지 다운로드 실패")
                    }
            }

            .addOnFailureListener {
                Log.d("jeon", "이미지 불러오기 실패")
            }

    }
}