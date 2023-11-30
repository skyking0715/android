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
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

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
    lateinit var profile: String
    lateinit var title:String
    lateinit var desc:String
    //Shop 클래스는 대표 이미지와 가게이름
    constructor(profile: String, title: String, desc:String):this(){
        this.profile=profile
        this.title=title
        this.desc=desc
    }
}
class Stylist_4 : Fragment() {
    lateinit var binding:FragmentStylist4Binding
    lateinit var db: FirebaseFirestore
    lateinit var storage: FirebaseStorage
    lateinit var firestore: FirebaseFirestore
    lateinit var collectionRef: CollectionReference
    lateinit var storageRef: StorageReference
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
        storage= Firebase.storage
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

        getReqDatas()




      /*  datas.add(R.drawable.re1)
        datas.add(R.drawable.re2)
        datas.add(R.drawable.re3)
        datas.add(R.drawable.re4)
        datas.add(R.drawable.re5)*/

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.extraPic.layoutManager=layoutManager

        adapter = S4Adapter(datas)
        binding.extraPic.adapter = adapter

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
                    Log.d("Jeon","datas:$reqDoc")
                    //val request=Request(reqDoc.profile,reqDoc.title,reqDoc.desc) //이미지리스트 제외 리퀘스트 정보 가져오기
                    //adapter.addRequest(request)
                }
            }
            .addOnFailureListener {
                Log.w("Jeon", "Error getting documents")
            }

    }

    fun dataBinding(binding: FragmentStylistShopBinding, shopData:ShopInfo){
        binding.shopName.text=shopData.shopName
        binding.shopAddress.text=shopData.shopAddress
        binding.priceText.text=shopData.priceList
        binding.shopNum.text=shopData.shopNum
        binding.shopDesc.text=shopData.shopDesc

        storageRef.listAll()
            .addOnSuccessListener { result->
                val urlList= mutableListOf<Uri>()

                val tasks=result.items.map{item->
                    item.downloadUrl.addOnSuccessListener { imageUri->
                        urlList.add(imageUri)
                    }

                }

                Tasks.whenAllSuccess<Unit>(tasks)
                    .addOnSuccessListener {
                        for((index,uri) in urlList.withIndex()){
                            when(index){
                                0-> Glide.with(this).load(uri).into(binding.img0)
                                1-> Glide.with(this).load(uri).into(binding.img1)
                                2-> Glide.with(this).load(uri).into(binding.img2)
                                3-> Glide.with(this).load(uri).into(binding.img3)
                                4-> Glide.with(this).load(uri).into(binding.img4)
                            }
                        }
                    }.addOnFailureListener {
                        Log.d("jeon", "이미지 다운로드 실패")
                    }

            }
            .addOnFailureListener {
                Log.d("jeon", "이미지 불러오기 실패")
            }

    }
}



class S4ViewHolder(val binding: ImgitemBinding):
    RecyclerView.ViewHolder(binding.root)

class S4Adapter(val datas: MutableList<Request>):
//@TODO 1: datas 원소타입 바꾸기
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun addRequest(request: Request) {
        datas.add(request)
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return S4ViewHolder(ImgitemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as S4ViewHolder).binding

        //val drawable = ContextCompat.getDrawable(binding.root.context, datas[position])
        //binding.imgData.setImageDrawable(drawable)

    }
}