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
import com.google.android.gms.tasks.Task
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resumeWithException

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
    private val menFrame: TrendMenFragment by lazy { TrendMenFragment() }
    private val womenFrame: TrendWomenFragment by lazy { TrendWomenFragment() }

    var photoList_men = arrayListOf<Uri>()
    var photoList_women = arrayListOf<Uri>()

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
        storageReference_trendMen=storage.reference.child("manTrend")
        storageReference_trendWomen=storage.reference.child("womanTrend")

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentStylistMainPageBinding.inflate(inflater, container, false)

        createFragments()
        return binding.root
    }
    private fun createFragments() {
        try{
            val menFrame = TrendMenFragment.newInstance(photoList_men)
            val womenFrame = TrendWomenFragment.newInstance(photoList_women)

            val fragmentTransaction = childFragmentManager.beginTransaction()

            // 프래그먼트 재사용하기
            if (menFrame.isAdded) {
                fragmentTransaction.show(menFrame)
            } else {
                fragmentTransaction.add(binding.frameView.id, menFrame, "menFrame")
            }
            if (womenFrame.isAdded) {
                fragmentTransaction.hide(womenFrame)
            } else {
                fragmentTransaction.add(binding.frameView.id, womenFrame, "womenFrame")
            }
            fragmentTransaction.commit()

            binding.trendTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.let {
                        when (it.position) {
                            0 -> showFragment(menFrame)
                            1 -> showFragment(womenFrame)
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }catch (e:Exception){
            Log.e("jeon", "Error in createFragments: ${e.message}")
            e.printStackTrace()
        }

    }

    private fun showFragment(fragment: Fragment) {
        val fragmentTransaction = childFragmentManager.beginTransaction()

        if (fragment.isAdded) {
            fragmentTransaction.show(fragment)
        } else {
            fragmentTransaction.hide(if (fragment == menFrame) womenFrame else menFrame)
            fragmentTransaction.add(binding.frameView.id, fragment, fragment.javaClass.simpleName)
        }

        fragmentTransaction.commit()
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


}
