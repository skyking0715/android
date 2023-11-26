package com.example.hairpick

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hairpick.databinding.FragmentClientMainPageBinding
import com.example.hairpick.databinding.FragmentStylistMainPageBinding
import com.example.hairpick.databinding.TrendimgitemBinding
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StylistMainPage.newInstance] factory method to
 * create an instance of this fragment.
 */
class StylistMainPage : Fragment() {
    lateinit var binding: FragmentStylistMainPageBinding
    lateinit var db: FirebaseFirestore
    lateinit var storage: FirebaseStorage
    lateinit var storageReference_trendMen: StorageReference
    lateinit var storageReference_trendWomen: StorageReference

    val photoList_men = mutableListOf<Uri>()
    val photoList_women = mutableListOf<Uri>()

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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentStylistMainPageBinding.inflate(inflater, container, false)



       /* val myInfo=getShopObject{ //유저정보 객체
                clientInfo ->
            if(clientInfo!=null){
                MyAccountApplication.name=clientInfo.name
                MyAccountApplication.address=clientInfo.shopAddress

                storageReference_trendMen=storage.reference.child("manTrend")
                storageReference_trendWomen=storage.reference.child("womanTrend")
                //getTrendImg()

            }else{
                Log.d("jeon", "데이터 로드 실패")
            }
        }*/







        val menFrame=TrendMenFragment()
        val womenFrame=TrendWomenFragment()

        val fragmentTransaction=childFragmentManager.beginTransaction()

        fragmentTransaction.add(binding.frameView.id, menFrame)
        fragmentTransaction.commit()

       binding.trendTabs.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    when (it.position) {
                        0 -> replaceFragment(menFrame)
                        1 -> replaceFragment(womenFrame)
                        // 다른 탭에 대한 처리 추가 가능
                    }
                }

            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        return binding.root
    }

    private fun replaceFragment(fragment: Fragment) {
        try{
            // 프래그먼트 트랜잭션을 시작하고 프래그먼트를 교체합니다.
            val fragmentTransaction = childFragmentManager.beginTransaction()
            fragmentTransaction.replace(binding.frameView.id, fragment)
            fragmentTransaction.commit()

            Log.d("jeon", "Fragment replaced successfully") // 로그 추가
        }catch (e: Exception) {
            Log.e("jeon", "Error replacing fragment: ${e.message}")
        }

    }
    fun getShopObject(callback: (ShopInfo?)->Unit){
        var stylistInfo:ShopInfo?=null
        val docRef=db.collection("stylists").document(MyAccountApplication.email.toString())
        docRef.get().addOnSuccessListener { document->
            stylistInfo=document.toObject(ShopInfo::class.java)
            callback(stylistInfo)

        }.addOnFailureListener {
            Log.d("jeon","사용자 데이터 가져오기 실패")
            callback(null)
        }

    }
    fun getTrendImg(){
        storageReference_trendMen.listAll()
            .addOnSuccessListener { result->
                for(item in result.items){
                    item.downloadUrl.addOnSuccessListener {imageUri->
                        //photoAdapter_trend.addPhoto(imageUri)
                        photoList_men.add(imageUri)
                    }
                        .addOnFailureListener {
                            Log.d("jeon","이미지 다운로드 url 가져오기 실패")
                        }

                }
            }
            .addOnFailureListener {
                Log.d("jeon", "이미지 불러오기 실패")
            }

        storageReference_trendWomen.listAll()
            .addOnSuccessListener { result->
                for(item in result.items){
                    item.downloadUrl.addOnSuccessListener {imageUri->
                        //photoAdapter_trend.addPhoto(imageUri)
                        photoList_women.add(imageUri)
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
}
