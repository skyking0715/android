package com.example.hairpick

import android.content.Context
import android.content.DialogInterface.OnClickListener
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.hairpick.databinding.FragmentClient3Binding
import com.example.hairpick.databinding.ShopDataBinding
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.net.URL

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class Shop(){
    lateinit var imageUrl: String
    lateinit var name:String
    lateinit var address:String
    //Shop 클래스는 대표 이미지와 가게이름
    constructor(imageUrl: String, name: String, address:String):this(){
        this.imageUrl=imageUrl
        this.name=name
        this.address=address
    }
}
class ShopViewHolder(val binding: ShopDataBinding):
    RecyclerView.ViewHolder(binding.root){

    fun bind(photoUrl: String){
        //Glide - 구글에서 만든 이미지 로더 라이브러리
        //기술문서 작성 시 추가할 것
        Glide.with(binding.imageData.context)
            .load(photoUrl)
            .into(binding.imageData)
    }

    }

class ShopAdapter(val datas: MutableList<Shop>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var shopInfo:MutableList<ShopInfo> = mutableListOf()
    fun saveShopInfo(shopinfo:ShopInfo){
        shopInfo.add(shopinfo)
    }
    fun addShop(shop: Shop) {
        datas.add(shop)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            RecyclerView.ViewHolder =ShopViewHolder(ShopDataBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        val binding = (holder as ShopViewHolder).binding
        holder.bind(datas[position].imageUrl)
        binding.shopNameText.text = datas[position].name
        binding.shopAddressText.text=datas[position].address
        binding.root.setOnClickListener{
            Log.d("Jeon", shopInfo.get(position).name)
            onItemClickCallback?.invoke(shopInfo[position])
            datas.clear() //화면 이동 시 리스트 초기화

        }


    }
    override fun getItemCount(): Int {
        return datas.size
    }
    // 아이템 클릭 리스너
    private var onItemClickListener: ((Int) -> Unit)? = null

    // 아이템 클릭 이벤트 설정 메서드
    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    private var onItemClickCallback: ((ShopInfo) -> Unit)? = null

    fun setOnItemClickCallback(callback: (ShopInfo) -> Unit) {
        onItemClickCallback = callback
    }



}


/**
 * A simple [Fragment] subclass.
 * Use the [Client_3.newInstance] factory method to
 * create an instance of this fragment.
 */
class Client_3 : Fragment() {
    lateinit var db: FirebaseFirestore
    lateinit var storage: FirebaseStorage
    lateinit var firestore:FirebaseFirestore
    lateinit var collectionRef:CollectionReference
    lateinit var datas:MutableList<Shop>
    lateinit var adapter:ShopAdapter
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
        collectionRef=firestore.collection("stylists")
        datas = mutableListOf<Shop>()
    }
   lateinit var binding:FragmentClient3Binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentClient3Binding.inflate(inflater,container,false)
        val layoutManager = LinearLayoutManager(activity)
        //xml파일에 recyclerView가 있어야함
        binding.nearShop.layoutManager=layoutManager

        adapter = ShopAdapter(datas)
        binding.nearShop.adapter = adapter

        adapter.setOnItemClickCallback { clickedShopInfo->
            navigateToClient4(clickedShopInfo)
        }

        binding.location.text =MyAccountApplication.address
        getShopDatas()


        return binding.root
    }
    fun getShopDatas(){

        val searchAddress:String=MyAccountApplication.address.toString()
        Log.d("Jeon", searchAddress)
        collectionRef.whereEqualTo("shopAddress",searchAddress)
            .get()
            .addOnSuccessListener {
                for(document in it){
                    val shopDoc=document.toObject(ShopInfo::class.java)
                    val shop=Shop(shopDoc.img,shopDoc.shopName,shopDoc.shopAddress)
                    adapter.addShop(shop)
                    adapter.saveShopInfo(shopDoc)
                }
            }
            .addOnFailureListener {
                Log.w("Jeon", "Error getting documents")
            }

    }
    private fun navigateToClient4(shopInfo: ShopInfo) {
        val fragment = Client_4()
        val bundle = Bundle()
        bundle.putString("shopId",shopInfo.id)
        fragment.arguments = bundle

        // 프래그먼트 트랜잭션 시작
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.addToBackStack(null) //백 스택 사용
        if (transaction != null) {
            // R.id.frameView는 ClientMainFrame의 프래임 레이아웃입니다.
            transaction.replace(R.id.frameView, fragment)
            transaction.addToBackStack(null)
            transaction.commit()

        } else {
            Log.e("Jeon", "Activity is null.")
        }
    }

}